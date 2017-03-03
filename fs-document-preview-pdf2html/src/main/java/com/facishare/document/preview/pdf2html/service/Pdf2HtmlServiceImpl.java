package com.facishare.document.preview.pdf2html.service;


import com.facishare.document.preview.api.model.arg.Pdf2HtmlArg;
import com.facishare.document.preview.api.model.result.Pdf2HtmlResult;
import com.facishare.document.preview.api.service.Pdf2HtmlService;
import com.facishare.document.preview.pdf2html.utils.CssHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuq on 2017/2/9.
 */
@Slf4j
public class Pdf2HtmlServiceImpl implements Pdf2HtmlService {
    private final ThreadFactory factory =
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("excuteCmd-%d").build();
    private final ExecutorService executorService = Executors.newCachedThreadPool(factory);
    @Override
    public Pdf2HtmlResult convertPdf2Html(Pdf2HtmlArg arg) {
        String filePath = arg.getOriginalFilePath();
        int page = arg.getPage() + 1;
        String dirName = FilenameUtils.getBaseName(FilenameUtils.getFullPathNoEndSeparator(filePath));
        String dataFilePath = doConvert(page, filePath, dirName);
        Pdf2HtmlResult result = Pdf2HtmlResult.builder().dataFilePath(dataFilePath).build();
        return result;
    }

    private  String doConvert(int page, String filePath, String dirName) {
        String dataFilePath = "";
        try {
            executeCmd(page, filePath);
            dataFilePath = handleResult(page, filePath, dirName);
        } catch (IOException e) {
            log.error("pdf2html happened iOException!", e);
        } catch (InterruptedException e) {
            log.error("pdf2html happened interruptedException!", e);
        } finally {
            return dataFilePath;
        }
    }

    private boolean  asyncExcute(String cmd) throws InterruptedException, ExecutionException, TimeoutException {
        Future<Boolean> future = executorService.submit(() -> exec(cmd));
        try {
            return future.get(30, TimeUnit.SECONDS);
        } finally {
            if (!future.isDone()) {
                log.warn("cancel it because timeout");
                future.cancel(true);
            }
        }
    }

    private  boolean executeCmd(int page, String filePath) throws IOException, InterruptedException, TimeoutException, ExecutionException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("begin convert pdf2html");
        String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String outPutDir = FilenameUtils.concat(basedDir, "p" + page);
        StringBuilder paramsBuilder = new StringBuilder();
        paramsBuilder.append("pdf2htmlEX");//命令行开始
        paramsBuilder.append(" -f " + page + " -l " + page);//一页页的转换
        paramsBuilder.append(" --fit-width 1000");//缩放
        paramsBuilder.append(" --embed-outline 0");//链接文件单独输出
        paramsBuilder.append(" --embed-css 0");
        paramsBuilder.append(" --css-filename css" + page + ".css");
        paramsBuilder.append(" --split-pages 1");
        paramsBuilder.append(" --embed-image 0");
        paramsBuilder.append(" --bg-format jpg");
        paramsBuilder.append(" --process-outline 0");
        paramsBuilder.append(" --optimize-text 1");
        paramsBuilder.append(" --embed-javascript 0");//js文件单独引用
        paramsBuilder.append(" --dest-dir " + outPutDir);//输出目录
        paramsBuilder.append(" " + filePath);
        String cmd = paramsBuilder.toString();
        log.info("cmd:{}", cmd);
        boolean result = asyncExcute(cmd);
        stopWatch.stop();
        log.info("end convert pdf2html,ret:{},cost:{}ms", result, stopWatch.getTime());
        return result;
    }


    public  boolean exec(String command) throws IOException, InterruptedException {
        log.info("执行脚本:" + command);
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor(30,TimeUnit.SECONDS);
            int exitValue= process.exitValue();
            if (process != null) {
                process.destroy();
            }
            return exitValue == 0;
        } catch (Exception e) {
            log.error("exe cmd happened exception", e);
            return false;
        }
    }


    private  String handleResult(int page, String filePath, String dirName) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("begin handle html!");
        String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String pageDirPath = FilenameUtils.concat(basedDir, "p" + page);
        String cssName = "css" + page + ".css";
        String cssPath = FilenameUtils.concat(pageDirPath, cssName);
        File cssFile = new File(cssPath);
        String newCssPath = FilenameUtils.concat(basedDir, cssName);
        String pageName = FilenameUtils.getBaseName(filePath) + page + ".page";
        String pagePath = FilenameUtils.concat(pageDirPath, pageName);
        String newPageName = page + ".html";
        File pageFile = new File(pagePath);
        String newPagePath = FilenameUtils.concat(basedDir, newPageName);
        handleCss(page, cssFile, newCssPath);
        handleHtml(page, pageFile, newPagePath, dirName);
        handleStaticResource(basedDir, pageDirPath);
        FileUtils.deleteDirectory(new File(pageDirPath));
        stopWatch.stop();
        log.info("end handle html,cost:{}ms", stopWatch.getNanoTime());
        return newPagePath;
    }


    private  void handleStaticResource(String destDirPath, String pageDirPath) throws IOException {
        Path path = Paths.get(pageDirPath);
        Files.list(path).filter(i -> i.toString().endsWith(".jpg")).forEach(f ->
        {
            File file = f.toFile();
            String name = FilenameUtils.getName(file.getAbsolutePath());
            String newFileName = FilenameUtils.concat(destDirPath, name);
            File newFile = new File(newFileName);
            if (!newFile.exists()) {
                file.renameTo(newFile);
            }
        });
    }


    private  void handleHtml(int page, File pageFile, String newPageFilePath, String dirName) throws IOException {
        String pageContent = FileUtils.readFileToString(pageFile);
        String style1 = "pf w0 h0";
        String style2 = "pf ww" + page + " hh" + page;
        pageContent = pageContent.replace(style1, style2);
        Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(pageContent);
        while (m.find()) {
            String srcStr = m.group();
            String newSrcStr = srcStr.replace("src=\"", "src=\"./" + dirName + "/");
            pageContent = pageContent.replace(srcStr, newSrcStr);
        }
        File newPageFile = new File(newPageFilePath);
        FileUtils.writeByteArrayToFile(newPageFile, pageContent.getBytes());
    }

    private  void handleCss(int page, File cssFile, String newCssFilePath) throws IOException {
        String cssContent = FileUtils.readFileToString(cssFile);
        String newCssContent = CssHandler.reWrite(cssContent, page);
        File newCssFile = new File(newCssFilePath);
        FileUtils.writeByteArrayToFile(newCssFile, newCssContent.getBytes());
    }

}
