package com.facishare.document.preview.pdf2html.utils;

import com.zaxxer.nuprocess.NuAbstractProcessHandler;
import com.zaxxer.nuprocess.NuProcess;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * Created by liuq on 2017/3/6.
 */
@Slf4j
public class ProcessHandler extends NuAbstractProcessHandler {
    private NuProcess nuProcess;

    @Override
    public void onStart(NuProcess nuProcess) {
        this.nuProcess = nuProcess;
    }

    @Override
    public boolean onStdinReady(ByteBuffer buffer) {
        buffer.flip();
        return false; // false means we have nothing else to write at this time
    }

    @Override
    public void onStdout(ByteBuffer buffer, boolean closed) {
        if (!closed) {
            byte[] bytes = new byte[buffer.remaining()];
            // You must update buffer.position() before returning (either implicitly,
            // like this, or explicitly) to indicate how many bytes your handler has consumed.
            buffer.get(bytes);
            log.info(new String(bytes));
            // For this example, we're done, so closing STDIN will cause the "cat" process to exit
            nuProcess.closeStdin(true);
        }
    }
}