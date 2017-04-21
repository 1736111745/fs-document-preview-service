package com.facishare.document.preview.asyncconvertor.model;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by liuq on 2017/4/21.
 */
@Slf4j
public class StreamGobbler extends Thread {
    private InputStream inputStream;
    private String streamType;
    private StringBuilder buf;
    private volatile boolean isStopped = false;


    public StreamGobbler(final InputStream inputStream, final String streamType) {
        this.inputStream = inputStream;
        this.streamType = streamType;
        this.buf = new StringBuilder();
        this.isStopped = false;
    }

    /**
     * Consumes the output from the input stream and displays the lines
     * consumed if configured to do so.
     */
    @Override
    public void run() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.buf.append(line + "\n");
            }
        } catch (IOException ex) {
            log.trace("Failed to successfully consume and display the input stream of type "
                    + streamType + ".", ex);
        } finally {
            this.isStopped = true;
            synchronized (this) {
                notify();
            }
        }
    }

    public String getContent() {
        if (!this.isStopped) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException ignore) {
                }
            }
        }
        return this.buf.toString();
    }
}