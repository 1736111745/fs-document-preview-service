package com.facishare.document.preview.pdf2html.service;


import com.facishare.document.preview.api.model.arg.Pdf2HtmlArg;
import com.facishare.document.preview.api.model.result.Pdf2HtmlResult;
import com.facishare.document.preview.api.service.Pdf2HtmlService;
import com.facishare.document.preview.pdf2html.utils.CssHandler;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

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

/**
 * Created by liuq on 2017/2/9.
 */
@Slf4j
public class Pdf2HtmlServiceImpl implements Pdf2HtmlService {

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
        List<String> args = createProcessArgs(page, filePath);
        Future<ProcessResult> future;
        try {
            future = new ProcessExecutor().command(args).start().getFuture();
            try {
                ProcessResult processResult = future.get(15, TimeUnit.SECONDS);
                if (processResult.getExitValue() == 0) {
                    dataFilePath = handleResult(page, filePath, dirName);
                }
            } catch (InterruptedException e) {
                log.error("do convert happened exception!", e);
            } catch (ExecutionException e) {
                log.error("do convert happened exception!", e);
            } catch (TimeoutException e) {
                log.error("do convert happened exception!", e);
            }
        } catch (IOException e) {
            log.error("get future fail!", e);
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
        return args;
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
        String pageName = FilenameUtils.getBaseName(filePath) + page + ".page";
        String pagePath = FilenameUtils.concat(pageDirPath, pageName);
        String newPageName = page + ".html";
        File pageFile = new File(pagePath);
        String newPagePath = FilenameUtils.concat(basedDir, newPageName);
        handleHtml(page, cssFile, pageFile, newPagePath, dirName);
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


    private void handleHtml(int page, File cssFile, File pageFile, String newPageFilePath, String dirName) throws IOException {
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
        String cssContent = FileUtils.readFileToString(cssFile);
        String newCssContent = CssHandler.reWrite(cssContent, page);
        String newPageContent = "<style>" + newCssContent + "</style>" + pageContent;
        File newPageFile = new File(newPageFilePath);
        FileUtils.writeByteArrayToFile(newPageFile, newPageContent.getBytes());
    }

}
