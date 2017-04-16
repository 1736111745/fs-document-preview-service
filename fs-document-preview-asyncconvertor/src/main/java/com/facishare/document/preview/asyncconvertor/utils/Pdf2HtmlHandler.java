package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        try {
            Future<ProcessResult> future = new ProcessExecutor()
                    .command(args)
                    .destroyOnExit()
                    .timeout(pdf2HtmlTimeout, TimeUnit.SECONDS)
                    .exitValueAny()
                    .readOutput(true)
                    .start().getFuture();
            ProcessResult processResult = future.get(pdf2HtmlTimeout, TimeUnit.SECONDS);
            if (processResult.getExitValue() == 0) {
                dataFilePath = handleResult(page, filePath, outPutDir);
            } else
                log.error("output:{},exit code:{}", processResult.outputUTF8(), processResult.getExitValue());
        } catch (IOException e) {
            log.error("do convert happened IOException!", e);
        } catch (InterruptedException e) {
            log.error("do convert happened InterruptedException!", e);
        } catch (ExecutionException e) {
            log.error("do convert happened ExecutionException!", e);
        } catch (TimeoutException e) {
            log.error("do convert happened TimeoutException!filePath:{},page:{}", filePath, page, e);
        } finally {
            FileUtils.deleteQuietly(new File(filePath));
        }
        return dataFilePath;
    }

    private static List<String> createProcessArgs(String filePath, String outPutDir, int page, int type) {
        if (type == 1)
            page = 1;
        List<String> args = Lists.newArrayList();
        args.add("pdf2htmlEX");//命令行开始
        args.add("-f");
        args.add(String.valueOf(page));
        args.add("-l");
        args.add(String.valueOf(page));
        args.add("--fit-width");//缩放
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
        log.info(StringUtils.join(args, "  "));
        return args;
    }


    private String handleResult(int page, String filePath, String outPutDir) throws IOException {
        String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String dataFileName = FilenameUtils.getBaseName(filePath) + ".html";
        String dataFilePath = FilenameUtils.concat(outPutDir, dataFileName);
        String pageName = page + ".html";
        String pagePath = FilenameUtils.concat(baseDir, pageName);
//        File dataFile = new File(dataFilePath);
//        File pageFile = new File(pagePath);
//        String dirName = FilenameUtils.getBaseName(baseDir);
//        String cssFileName = FilenameUtils.getBaseName(filePath) + ".css";
//        String cssFileFilePath = FilenameUtils.concat(outPutDir, cssFileName);
//        String newCssFilePath = FilenameUtils.concat(baseDir, page + ".css");
//        File cssFile = new File(cssFileFilePath);
//        cssFile.renameTo(new File(newCssFilePath));
//        //处理背景图片
//        String bgFileFilePath = FilenameUtils.concat(outPutDir, "bg1.jpg");
//        String newBgFilePath = FilenameUtils.concat(baseDir, "bg" + page + ".jpg");
//        File bgFile = new File(bgFileFilePath);
//        bgFile.renameTo(new File(newBgFilePath));
//        handleHtml(dataFile, pageFile, page, dirName);
//        FileUtils.deleteDirectory(new File(outPutDir));
        return pagePath;
    }

    private void handleHtml(File dataFile, File pageFile, int page, String dirName) throws IOException {
        String html = FileUtils.readFileToString(dataFile);
        html = html.replace("base.min.css", "../static/css/base.min.css");
        html = html.replace("<link rel=\"stylesheet\" href=\"fancy.min.css\"/>", "");
        String cssLink = "css" + page + ".css";
        html = html.replace(cssLink, "./" + dirName + "/" + cssLink);
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
        //html = html.replace("src=\"", "src=\"./" + dirName + "/");
        String cssFileName = FilenameUtils.getBaseName(dataFile.getName()) + ".css";
        html = html.replace(cssFileName, "./" + dirName + "/" + page + ".css");
        html = html.replace("bg1.jpg", "./" + dirName + "/bg" + page + ".jpg");
        html = html.replace("\n", "");
        FileUtils.writeByteArrayToFile(pageFile, html.getBytes());
    }
}
