package com.facishare.document.preview.cgi.utils;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.time.StopWatch;

import java.io.*;

/**
 * Created by liuq on 16/9/22.
 */
public class ImageHandle {
    public static void convertToPng(String svgFilePath, OutputStream outputStream)
            throws TranscoderException, IOException {
        try {
            InputStream inputStream = new FileInputStream(svgFilePath);
            PNGTranscoder t = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(inputStream);
            TranscoderOutput output = new TranscoderOutput(outputStream);
            t.transcode(input, output);
            outputStream.flush();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, TranscoderException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String svgFile = "/share/docconvert/svg/4.svg";
        File file = new File("/share/docconvert/png/4.png");
        OutputStream outputStream = new FileOutputStream(file);
        stopWatch.stop();
        convertToPng(svgFile, outputStream);
        System.out.println(stopWatch.getNanoTime());
    }
}
