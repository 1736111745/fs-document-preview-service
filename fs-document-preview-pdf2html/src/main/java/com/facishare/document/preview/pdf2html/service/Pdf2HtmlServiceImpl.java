package com.facishare.document.preview.pdf2html.service;


import com.facishare.document.preview.api.model.arg.Pdf2HtmlArg;
import com.facishare.document.preview.api.model.result.Pdf2HtmlResult;
import com.facishare.document.preview.api.service.Pdf2HtmlService;
import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuq on 2017/2/9.
 */
public class Pdf2HtmlServiceImpl implements Pdf2HtmlService {
    @Override
    public Pdf2HtmlResult convertPdf2Html(Pdf2HtmlArg arg) {
        return null;
    }

//    private final static  LessEngine engine = new LessEngine();

    private static void executeCmd(int page, String filePath) throws IOException, InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String outPutDir = FilenameUtils.concat(basedDir, "p" + page);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("pdf2htmlEX");//命令行开始
        stringBuilder.append(" -f " + page + " -l " + page);//一页页的转换
        stringBuilder.append(" --zoom 1");//缩放
        stringBuilder.append(" --embed-outline 0");//链接文件单独输出
        stringBuilder.append(" --embed-css 0");
        stringBuilder.append(" --css-filename css" + page + ".css");
        stringBuilder.append(" --split-pages 1");
        stringBuilder.append(" --embed-font 0");
        stringBuilder.append(" --embed-image 0");
        stringBuilder.append(" --embed-javascript 0");//js文件单独引用
        stringBuilder.append(" --dest-dir " + outPutDir);//输出目录
        stringBuilder.append(" " + filePath);
        String cmd = stringBuilder.toString();
        String[] cmds = {"/bin/sh", "-c", cmd};
        Process pro = Runtime.getRuntime().exec(cmds);
        int ret = pro.waitFor();
        stopWatch.stop();
        System.out.println("ret:" + ret + ",cost:" + stopWatch.getTime() + "ms");
    }

    private static void handleResult(int page, String filePath) throws IOException {
        String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String jsDirPath = FilenameUtils.concat(basedDir, "js");
        File jsDir = new File(jsDirPath);
        if (!jsDir.exists()) {
            jsDir.mkdir();
        }
        String pageDirPath = FilenameUtils.concat(basedDir, "p" + page);
        String cssName = "css" + page + ".css";
        String cssPath = FilenameUtils.concat(pageDirPath, cssName);
        File cssFile = new File(cssPath);
        String newCssPath = FilenameUtils.concat(jsDirPath, cssName);
        String pageName = FilenameUtils.getBaseName(filePath) + page + ".page";
        String pagePath = FilenameUtils.concat(pageDirPath, pageName);
        File pageFile = new File(pagePath);
        String newPagePath = FilenameUtils.concat(basedDir, pageName);
        File newPageFile = new File(newPagePath);
        pageFile.renameTo(newPageFile);
        handleCss(page, cssFile, newCssPath);
        FileUtils.deleteDirectory(new File(pageDirPath));

    }

    private static void handleCss(int page, File cssFile, String newCssFilePath) throws IOException {
        String idStr = "#pf" + page;
        String cssContent = FileUtils.readFileToString(cssFile);
        String w0 = "", h0 = "", m_w0 = "", m_h0 = "";
        String patternW0 = ".w0\\{.*}";
        Pattern rW0 = Pattern.compile(patternW0);
        Matcher mW0 = rW0.matcher(cssContent);
        if (mW0.find()) {
            w0 = mW0.group();
        }
        if (mW0.find()) {
            m_w0 = mW0.group();
        }
        String patternH0 = ".h0\\{.*}";
        Pattern rH0 = Pattern.compile(patternH0);
        Matcher mH0 = rH0.matcher(cssContent);
        if (mH0.find()) {
            h0 = mH0.group();
        }
        if (mH0.find()) {
            m_h0 = mH0.group();
        }
        String lessContent = idStr + "{" + cssContent + "}";
        String lessFileName = "css" + page + ".less";
        String lessFilePath = FilenameUtils.concat(FilenameUtils.getFullPath(cssFile.getAbsolutePath()), lessFileName);
        File lessFile = new File(lessFilePath);
        FileUtils.writeByteArrayToFile(lessFile, lessContent.getBytes());
        File newCssFile = new File(newCssFilePath);
        //engine.compile(lessFile, newCssFile);
        //修正
        String newCssContent = FileUtils.readFileToString(newCssFile);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(newCssContent);
        if (!Strings.isNullOrEmpty(w0))
            stringBuilder.append(w0 + "\r\n");
        if (!Strings.isNullOrEmpty(h0))
            stringBuilder.append(w0 + "\r\n");
        stringBuilder.append("@media print{\r\n");
        if (!Strings.isNullOrEmpty(m_w0))
            stringBuilder.append(w0 + "\r\n");
        if (!Strings.isNullOrEmpty(m_h0))
            stringBuilder.append(w0 + "\r\n");
        stringBuilder.append("}");
        newCssContent = stringBuilder.toString();
        FileUtils.writeByteArrayToFile(newCssFile, newCssContent.getBytes());
    }

    public static void main(String[] args) throws IOException {
//        String filePath = "/Users/liuq/Downloads/pdf/a.pdf";
//        for (int i = 1; i < 10; i++) {
//            executeCmd(i, filePath);
//            handleResult(i, filePath);
//        }
//        LessEngine engine = new LessEngine();

    }
}
