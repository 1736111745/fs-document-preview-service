package com.facishare.document.preview.provider.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.time.StopWatch;

import java.io.*;

/**
 * Created by liuq on 16/9/22.
 */
@Slf4j
public class ImageHandler {

    public static boolean convertSvgToPng(String svgFilePath, String pngFilePath)throws IOException {
        log.info("begin convert svg to png,svgFilePath:{},pngFilePath:{}", svgFilePath, pngFilePath);
        boolean success = true;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        InputStream inputStream = new FileInputStream(svgFilePath);
        OutputStream outputStream = new FileOutputStream(pngFilePath);
        PNGTranscoder pngTranscoder = new PNGTranscoder();
        try {
            TranscoderInput input = new TranscoderInput(inputStream);
            TranscoderOutput output = new TranscoderOutput(outputStream);
            pngTranscoder.transcode(input, output);
            outputStream.flush();
        } catch (Exception e) {
            success = false;
            log.info("convert svg to png happened error\r\n", e);
        } finally {
            stopWatch.stop();
            log.info("convert svg to png done,svgFilePath:{},cost:{}", svgFilePath, stopWatch.getTime() + "ms");
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return success;
        }
    }

    public static void main(String[] args) throws IOException, TranscoderException {
        String svgFile = "/Users/liuq/Downloads/pic/6.svg";
        String pngFilePath = "/Users/liuq/Downloads/pic/xxxxx.png";
        ImageHandler.convertSvgToPng(svgFile, pngFilePath);
    }
}
