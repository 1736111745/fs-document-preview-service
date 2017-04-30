package com.facishare.document.preview.asyncconvertor.utils;

import com.aspose.pdf.Font;
import com.aspose.pdf.Operator;
import com.aspose.pdf.SoundAnnotation;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.aspectj.weaver.ast.Var;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;
import org.zeroturnaround.process.SystemProcess;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by liuq on 2017/3/7.
 */
@Slf4j
@Component
public class Pdf2HtmlHandler {
    @ReloadableProperty("pdf2HtmlTimeout")
    private int pdf2HtmlTimeout = 60;


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
            } else
                log.error("output:{},exit code:{}", result.outputUTF8(), result.getExitValue());
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
        Pattern pattern = Pattern.compile(regex,Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(cssHtml);
        while (matcher.find()) {
            String fontIndex = matcher.group();
            //找到字体
            String fontFilePath = FilenameUtils.concat(outPutDir, "f" + fontIndex + ".woff");
            String newFontName = "font" + page + "_f" + fontIndex + ".woff";
            String newFontFilePath = FilenameUtils.concat(baseDir, newFontName);
            File fontFile = new File(fontFilePath);
            if (fontFile.exists()) {
                File newFontFile = new File(newFontFilePath);
                fontFile.renameTo(newFontFile);
                String fontStyle = "url(f" + fontIndex + ".woff)";
                String newFontStyle = "url('./" + dirName + "/" + newFontName + "')";
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

    public static void main(String[] args) {

        String html="@font-face{font-family:ff1;src:url(f1.woff)format(\"woff\");}.ff1{font-family:ff1;line-height:0.908542;font-style:normal;font-weight:normal;visibility:visible;}\n" +
                "@font-face{font-family:ff2;src:url(f2.woff)format(\"woff\");}.ff2{font-family:ff2;line-height:0.908542;font-style:normal;font-weight:normal;visibility:visible;}\n" +
                "@font-face{font-family:ff3;src:url(f3.woff)format(\"woff\");}.ff3{font-family:ff3;line-height:1.051758;font-style:normal;font-weight:normal;visibility:visible;}\n" +
                "@font-face{font-family:ff4;src:url(f4.woff)format(\"woff\");}.ff4{font-family:ff4;line-height:1.051758;font-style:normal;font-weight:normal;visibility:visible;}\n" +
                "@font-face{font-family:ff5;src:url(f5.woff)format(\"woff\");}.ff5{font-family:ff5;line-height:1.015137;font-style:normal;font-weight:normal;visibility:visible;}\n" +
                ".m0{transform:matrix(0.419956,0.000000,0.000000,0.419956,0,0);-ms-transfo";
        String regex = "url\\(f\\d\\.woff\\)";
        Pattern pattern = Pattern.compile(regex,Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find())
        {
           String fontIndex=matcher.group();
            String fontStyle = "url(f" + fontIndex + ".woff)";
            String newFontStyle = "url('./33.woff')";
            cssHtml = cssHtml.replace(fontStyle, newFontStyle);
        }

    }
}
