package com.facishare.document.preview.pdf2html.utils;

import com.zaxxer.nuprocess.NuAbstractProcessHandler;
import com.zaxxer.nuprocess.NuProcess;
import com.zaxxer.nuprocess.NuProcessBuilder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuq on 2017/3/3.
 */
@Slf4j
public class ProcessUtils {

    public static int DoProcess(List<String> args) throws InterruptedException {
        NuProcessBuilder pb = new NuProcessBuilder(args);
        ProcessHandler handler = new ProcessHandler();
        pb.setProcessListener(handler);
        NuProcess process = pb.start();
        process.wantWrite();
        process.waitFor(30, TimeUnit.SECONDS);
        return 0;
    }
}
