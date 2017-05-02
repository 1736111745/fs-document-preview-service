package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.github.autoconf.ConfigFactory;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;
import org.zeroturnaround.process.SystemProcess;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuq on 2017/3/7.
 */
@Slf4j
@Component
public class Pdf2HtmlHandler {
    @ReloadableProperty("pdf2HtmlTimeout")
    private int pdf2HtmlTimeout = 60;

    private static Map<String, String> fontMap = new HashMap<>();


    static {
        ConfigFactory.getConfig("fs-dps-font-config", conf -> fontMap = conf.getAll());
    }


    public String doConvert(ConvertPdf2HtmlMessage message) throws IOException {
        String dataFilePath = "";
        String filePath = message.getFilePath();
        int page = message.getPage();
        int type = message.getType();
        String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String outPutDir = FilenameUtils.concat(basedDir, "p" + page);
        List<String> args = createProcessArgs(filePath, outPutDir, page, type);
        StartedProcess startedProcess = new ProcessExecutor()
                .command(args)
                .readOutput(false)
                .start();
        Process pdf2htmlProcess = startedProcess.getProcess();
        try {
            Future<ProcessResult> future = startedProcess.getFuture();
            ProcessResult result = future.get(pdf2HtmlTimeout, TimeUnit.SECONDS);
            if (result.getExitValue() == 0) {
                dataFilePath = handleResult(page, filePath, outPutDir, type);
            } else {
                log.error("do convert fail!exit value:{}", result.getExitValue());
            }
        } catch (IOException e) {
            log.error("do convert happened IOException!", e);
        } catch (InterruptedException e) {
            log.error("do convert happened InterruptedException!", e);
        } catch (TimeoutException e) {
            log.error("do convert happened TimeoutException!filePath:{},page:{}", filePath, page, e);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (pdf2htmlProcess != null) {
                SystemProcess process = Processes.newStandardProcess(pdf2htmlProcess);
                try {
                    ProcessUtil.destroyGracefullyOrForcefullyAndWait(process, 30, TimeUnit.SECONDS, 10, TimeUnit.SECONDS);
                    log.info("pdf2htmlProcess has been killed!");
                } catch (InterruptedException e) {
                    log.error("kill pdf2htmlProcess happened InterruptedException!", e);
                } catch (TimeoutException e) {
                    log.error("kill pdf2htmlProcess timeout!", e);
                }
            }
            if (type == 1) {
                FileUtils.deleteQuietly(new File(filePath));
            }
        }
        return dataFilePath;
    }

    private List<String> createProcessArgs(String filePath, String outPutDir, int page, int type) {
        if (type == 1)
            page = 1;
        List<String> args = Lists.newArrayList();
        args.add("pdf2htmlEX");//命令行开始
        args.add("-f");
        args.add(String.valueOf(page));
        args.add("-l");
        args.add(String.valueOf(page));
        args.add("--fit-width");//缩放px
        args.add("1000");
        args.add("--embed-outline");//链接文件单独输出
        args.add("0");
        args.add("--embed-css");
        args.add("0");
        if (type == 2) {
            args.add("--css-filename");
            args.add("css" + page + ".css");
        }
        args.add("--embed-image");
        args.add("0");
        args.add("--embed-font");
        args.add("0");
        args.add("--bg-format");
        args.add("jpg");
        args.add("--no-drm");
        args.add("1");
        args.add("--process-outline");
        args.add("0");
        args.add("--optimize-text");
        args.add("1");
        args.add("--correct-text-visibility");
        args.add("1");
        args.add("--embed-javascript");//js文件单独引用
        args.add("0");
        args.add("--dest-dir");//输出目录
        args.add(outPutDir);
        args.add(filePath);
        return args;
    }


    private String getFontName(String fontFile) {
        String fontName = "yahei";
        List<String> args = Lists.newArrayList();
        args.add("ttx");
        args.add("-t");
        args.add("name");
        args.add(fontFile);
        try {
            ProcessResult processResult = new ProcessExecutor().command(args).readOutput(false).timeout(1, TimeUnit.SECONDS).execute();
            if (processResult.getExitValue() == 0) {
                String fontDescFilePath = fontFile.replace("woff", "ttx");
                File fontDescFile = new File(fontDescFilePath);
                if (fontDescFile.exists()) {
                    String fontDesc = FileUtils.readFileToString(fontDescFile).toLowerCase();
                    log.info(fontDesc);
                    Iterator iterator = fontMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        String key = (String) entry.getKey();
                        if (fontDesc.indexOf(key) > -1) {
                            fontName = key;
                            break;
                        }
                    }
                }
            } else
                log.error("get font name fail,exit value:{}", processResult.getExitValue());
        } catch (IOException e) {
            log.error("do get font name  happened IOException!", e);
        } catch (InterruptedException e) {
            log.error("do get font name happened  InterruptedException!", e);
        } catch (TimeoutException e) {
            log.error("do get font name happened TimeoutException!font file:{}", fontFile);
        }
        return fontName;
    }

    private String handleResult(int page, String filePath, String outPutDir, int type) throws IOException {
        String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String dataFileName = FilenameUtils.getBaseName(filePath) + ".html";
        String dataFilePath = FilenameUtils.concat(outPutDir, dataFileName);
        String pageName = page + ".html";
        String pagePath = FilenameUtils.concat(baseDir, pageName);
        File dataFile = new File(dataFilePath);
        if (!dataFile.exists()) return "";
        File pageFile = new File(pagePath);
        String dirName = FilenameUtils.getBaseName(baseDir);
        String cssFileName = type == 1 ? FilenameUtils.getBaseName(filePath) + ".css" : "css" + page + ".css";
        String newCssFileName = page + ".css";
        String cssFileFilePath = FilenameUtils.concat(outPutDir, cssFileName);

        String cssHtml = FileUtils.readFileToString(new File(cssFileFilePath));
        String regex = "url\\(f\\d\\.woff\\)";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(cssHtml);
        while (matcher.find()) {
            String fontStyle = matcher.group();
            String fontName = fontStyle.replace("url(", "").replace(")", "");
            //找到字体
            String fontFilePath = FilenameUtils.concat(outPutDir, fontName);
            File fontFile = new File(fontFilePath);
            if (fontFile.exists()) {
                String realFontName = getFontName(fontFilePath);
                String realFontUrl = fontMap.get(realFontName);
                String newFontStyle = "url(" + realFontUrl + ")";
                cssHtml = cssHtml.replace(fontStyle, newFontStyle);
            }
        }
        String newCssFilePath = FilenameUtils.concat(baseDir, newCssFileName);
        FileUtils.writeByteArrayToFile(new File(newCssFilePath), cssHtml.getBytes());


        //处理背景图片
        Path bgPath = Files.list(Paths.get(outPutDir)).filter(f -> f.toFile().getName().startsWith("bg")).findFirst().orElse(null);
        String bgName = "";
        String newBgName = "";
        if (bgPath != null) {
            File bgFile = bgPath.toFile();
            bgName = bgFile.getName();
            newBgName = "bg" + page + ".jpg";
            String newBgFilePath = FilenameUtils.concat(baseDir, newBgName);
            bgFile.renameTo(new File(newBgFilePath));
        }
        //处理字体


        handleHtml(outPutDir, dataFile, pageFile, dirName, cssFileName, newCssFileName, bgName, newBgName);
        return pagePath;
    }

    private void handleHtml(String outPutDir, File dataFile, File pageFile, String dirName, String cssName, String newCssName, String bgName, String newBgName) throws IOException {
        try {
            String html = FileUtils.readFileToString(dataFile);
            html = html.replace("<!-- Created by pdf2htmlEX (https://github.com/coolwanglu/pdf2htmlex) -->", "");
            html = html.replace("Evaluation Warning : The document was created with  Spire.Presentation for .NET", "");
            html = html.replace("base.min.css", "../static/css/base.min.css");
            html = html.replace("<link rel=\"stylesheet\" href=\"fancy.min.css\"/>", "");
            html = html.replace(cssName, "./" + dirName + "/" + newCssName);
            html = html.replace("<script src=\"compatibility.min.js\"></script>", "");
            html = html.replace("<script src=\"pdf2htmlEX.min.js\"></script>", "");
            html = html.replace("<script>\n" +
                    "try{\n" +
                    "pdf2htmlEX.defaultViewer = new pdf2htmlEX.Viewer({});\n" +
                    "}catch(e){}\n" +
                    "</script>", "");
            html = html.replace("<div id=\"sidebar\">\n" +
                    "<div id=\"outline\">\n" +
                    "</div>\n" +
                    "</div>", "");
            html = html.replace("<div class=\"loading-indicator\">", "");
            html = html.replace("<img alt=\"\" src=\"pdf2htmlEX-64x64.png\"/>", "");
            if (!Strings.isNullOrEmpty(bgName)) {
                html = html.replace(bgName, "./" + dirName + "/" + newBgName);
            }
            html = html.replace("\n", "");
            FileUtils.writeByteArrayToFile(pageFile, html.getBytes());
        } catch (Exception ex) {
            log.error("handelHtml happened exception", ex);
        } finally {
            FileUtils.deleteQuietly(new File(outPutDir));
        }
    }

    public static void main(String[] args) throws IOException, FontFormatException {

    }
}
