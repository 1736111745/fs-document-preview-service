package com.facishare.document.preview.pdf2html.service;
import com.facishare.document.preview.api.model.arg.Pdf2HtmlArg;
import com.facishare.document.preview.api.model.result.Pdf2HtmlResult;
import com.facishare.document.preview.api.service.Pdf2HtmlService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;

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
        stringBuilder.append(" --embed-css 1");
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

    private void  handleResult(int page,String filePath,String outPutDir)
    {
        String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        for(int i=0;i<30;i++) {
            executeCmd(i, "/Users/liuq/Downloads/pdf/p1.pdf");
        }
    }
}
