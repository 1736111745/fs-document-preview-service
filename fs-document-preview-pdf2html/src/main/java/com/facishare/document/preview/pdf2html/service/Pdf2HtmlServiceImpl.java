package com.facishare.document.preview.pdf2html.service;


import com.facishare.document.preview.api.model.arg.Pdf2HtmlArg;
import com.facishare.document.preview.api.model.result.Pdf2HtmlResult;
import com.facishare.document.preview.api.service.Pdf2HtmlService;
import com.facishare.document.preview.pdf2html.utils.CssHandler;
import com.facishare.document.preview.pdf2html.utils.ProcessUtils;
import com.google.common.collect.Lists;
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
import java.util.List;
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

    private String doConvert(int page, String filePath, String dirName) {

        String dataFilePath = "";
        try {
            boolean success = asyncExec(() -> executeCmd(page, filePath));
            if (success) {
                dataFilePath = handleResult(page, filePath, dirName);
            }
        } catch (Exception e) {
            dataFilePath = "";
        } finally {
            return dataFilePath;
        }
    }

    private <V> V asyncExec(Callable<V> callable) throws Exception {
        Future<V> future = executorService.submit(callable);
        try {
            return future.get(60, TimeUnit.SECONDS);
        } finally {
            if (!future.isDone()) {
                future.cancel(true);
            }
        }
    }

    private boolean executeCmd(int page, String filePath) throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("begin convert pdf2html");
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
        args.add("--split-pages");
        args.add("1");
        args.add("--embed-image");
        args.add("0");
        args.add("--bg-format");
        args.add("jpg");
        args.add("--process-outline");
        args.add("0");
        args.add("--optimize-text");
        args.add("1");
        args.add("--embed-javascript");//js文件单独引用
        args.add("0");
        args.add("--dest-dir");//输出目录
        args.add(outPutDir);
        args.add(filePath);
        boolean result = ProcessUtils.DoProcess(args);
        stopWatch.stop();
        log.info("end convert pdf2html,page:{},ret:{},cost:{}ms", page, result, stopWatch.getTime());
        return result;
    }


    private String handleResult(int page, String filePath, String dirName) throws IOException {
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
        log.info("end handle html,cost:{}ms", stopWatch.getTime());
        return newPagePath;
    }


    private void handleStaticResource(String destDirPath, String pageDirPath) throws IOException {
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


    private void handleHtml(int page, File pageFile, String newPageFilePath, String dirName) throws IOException {
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

    private void handleCss(int page, File cssFile, String newCssFilePath) throws IOException {
        String cssContent = FileUtils.readFileToString(cssFile);
        String newCssContent = CssHandler.reWrite(cssContent, page);
        File newCssFile = new File(newCssFilePath);
        FileUtils.writeByteArrayToFile(newCssFile, newCssContent.getBytes());
    }

}
