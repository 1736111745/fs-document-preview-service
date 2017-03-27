package com.facishare.document.preview.asyncconvertor.utils;

import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public String doConvert(int page, String filePath) {
        String dataFilePath = "";
        List<String> args = createProcessArgs(page, filePath);


        Future<ProcessResult> future;
        try {
            future = new ProcessExecutor()
                    .command(args)
                    .start().getFuture();
            ProcessResult processResult = future.get(pdf2HtmlTimeout, TimeUnit.SECONDS);
            if (processResult.getExitValue() == 0) {
                dataFilePath = handleResult(page, filePath);
            } else
                log.info("output:{},exit code:{}", processResult.outputUTF8(),processResult.getExitValue());
        } catch (IOException e) {
            log.error("do convert happened IOException!", e);
        } catch (InterruptedException e) {
            log.error("do convert happened InterruptedException!", e);
        } catch (ExecutionException e) {
            log.error("do convert happened ExecutionException!", e);
        } catch (TimeoutException e) {
            log.error("do convert happened TimeoutException!filePath:{},page:{}", filePath, page, e);
        }
        return dataFilePath;
    }


    private static List<String> createProcessArgs(int page, String filePath) {
        String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String outPutDir = FilenameUtils.concat(basedDir, "p" + page);
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
        args.add("--css-filename");
        args.add("css" + page + ".css");
        args.add("--embed-image");
        args.add("0");
        args.add("--bg-format");
        args.add("jpg");
        args.add("--hdpi");
        args.add("90");
        args.add("--vdpi");
        args.add("90");
        args.add("--no-drm");
        args.add("1");
        args.add("--process-outline");
        args.add("0");
        args.add("--optimize-text");
        args.add("1");
        args.add("--embed-javascript");//js文件单独引用
        args.add("0");
        args.add("--dest-dir");//输出目录
        args.add(outPutDir);
        args.add(filePath);
        return args;
    }

    private String handleResult(int page, String filePath) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("begin handle html!filePath:{},page:{}", filePath, page);
        String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String pageBaseDir = baseDir + "/p" + page;
        String dataFileName = FilenameUtils.getBaseName(filePath) + ".html";
        String dataFilePath = FilenameUtils.concat(pageBaseDir, dataFileName);
        String pageName = page + ".html";
        String pagePath = FilenameUtils.concat(baseDir, pageName);
        File dataFile = new File(dataFilePath);
        File pageFile = new File(pagePath);
        String dirName = FilenameUtils.getBaseName(baseDir);
        handleHtml(dataFile, pageFile, page, dirName);
        Files.list(Paths.get(pageBaseDir)).filter(file -> {
            String fileName = file.toFile().getName();
            return fileName.startsWith("css") || fileName.startsWith("bg");
        }).forEach(f -> {
            File file = f.toFile();
            String fileName = file.getName();
            String newFilePath = FilenameUtils.concat(baseDir, fileName);
            file.renameTo(new File(newFilePath));
        });
        FileUtils.deleteDirectory(new File(pageBaseDir));
        stopWatch.stop();
        log.info("end handle html!,filePath:{},page:{},cost:{}ms", filePath, page, stopWatch.getTime());
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
        html = html.replace("src=\"", "src=\"./" + dirName + "/");
        html = html.replace("\n", "");
        FileUtils.writeByteArrayToFile(pageFile, html.getBytes());
    }
}
