package com.facishare.document.preview.pdf2html.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by liuq on 2017/3/3.
 */
@Slf4j
public class Pdf2HtmlProcessHelper {

    public static boolean doProcess(int page, String filePath) throws IOException, TimeoutException, InterruptedException {
        List<String> args = createProcessArgs(page, filePath);
        Future<ProcessResult> future;
        try {
            future = new ProcessExecutor().command(args).start().getFuture();
            try {
                ProcessResult processResult = future.get(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("do convert happened interrupted exception!", e);
            } catch (ExecutionException e) {
                log.error("do convert happened  execution exception exception!", e);
            } catch (TimeoutException e) {
                log.error("do convert happened timeout exception!", e);
            }
        } catch (IOException e) {
            log.error("get future fail!", e);
        }
        return true;
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

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        String filePath = "/Users/liuq/Downloads/sfsfs.pdf";
        doProcess(1,filePath);

        Thread thread=new Thread(() -> {
            try {
                doProcess(1, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        Thread thread2=new Thread(() -> {
            try {
                doProcess(1, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread2.start();


        Thread thread1=new Thread(() -> {
            try {
                doProcess(1, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread1.start();

    }
}
