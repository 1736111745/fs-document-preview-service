package com.facishare.document.preview.pdf2html.service;
import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;
import com.facishare.document.preview.api.model.arg.Pdf2HtmlArg;
import com.facishare.document.preview.api.model.result.Pdf2HtmlResult;
import com.facishare.document.preview.api.service.Pdf2HtmlService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuq on 2017/2/9.
 */
public class Pdf2HtmlServiceImpl implements Pdf2HtmlService {
    @Override
    public Pdf2HtmlResult convertPdf2Html(Pdf2HtmlArg arg) {
        return null;
    }


    private static  void executeCmd(int page, String filePath) throws IOException, InterruptedException {
        StopWatch stopWatch=new StopWatch();
        stopWatch.start();
        String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String outPutDir = FilenameUtils.concat(basedDir, "p" + page);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("pdf2htmlEX");//命令行开始
        stringBuilder.append(" -f " + page + " -l " + page);//一页页的转换
        stringBuilder.append(" --zoom 1");//缩放
        stringBuilder.append(" --embed-outline 0");//链接文件单独输出
        stringBuilder.append(" --embed-css 0");
        stringBuilder.append(" --css-filename css"+page+".css");
        stringBuilder.append(" --split-pages 1");
        stringBuilder.append(" --embed-javascript 0");//js文件单独引用
        stringBuilder.append(" --dest-dir " + outPutDir);//输出目录
        stringBuilder.append(" " + filePath);
        String cmd = stringBuilder.toString();
        String[] cmds = {"/bin/sh", "-c", cmd};
        Process pro = Runtime.getRuntime().exec(cmds);
        int ret = pro.waitFor();
        stopWatch.stop();
        System.out.println("ret:"+ret+",cost:"+stopWatch.getTime()+"ms");
    }

    private static void  handleResult(int page, String filePath) throws IOException {
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
        File newCssFile = new File(newCssPath);
        cssFile.renameTo(newCssFile);
        String pageName = FilenameUtils.getBaseName(filePath) + page + ".page";
        String pagePath = FilenameUtils.concat(pageDirPath, pageName);
        File pageFile = new File(pagePath);
        String newPagePath = FilenameUtils.concat(basedDir, pageName);
        File newPageFile = new File(newPagePath);
        pageFile.renameTo(newPageFile);
        FileUtils.deleteDirectory(new File(pageDirPath));
    }

    public static void main(String[] args) throws IOException, InterruptedException, LessException {
        String filePath="/Users/liuq/Downloads/pdf/p1.pdf";
        for(int i=1;i<30;i++) {
            executeCmd(i, filePath);
            //handleResult(i, filePath);
        }
//        LessEngine engine = new LessEngine();
//        engine.compile(new File("/Users/liuq/Downloads/pdf/p1/css1.less"),new File("/Users/liuq/Downloads/pdf/p1/css3.css"));
    }
}
