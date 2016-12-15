package com.facishare.document.preview.cgi.utils;

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
    public static void convertSvgToJpg(String svgFilePath, String jpgFilePath)
            throws TranscoderException, IOException {
        InputStream inputStream = new FileInputStream(svgFilePath);
        OutputStream outputStream = new FileOutputStream(jpgFilePath);
        try {
            JPEGTranscoder jpegTranscoder = new JPEGTranscoder();
            TranscoderInput input = new TranscoderInput(inputStream);
            TranscoderOutput output = new TranscoderOutput(outputStream);
            jpegTranscoder.transcode(input, output);
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

    public static void convertSvgToPng(String svgFilePath, String pngFilePath)
            throws TranscoderException, IOException {
        StopWatch stopWatch=new StopWatch();
        stopWatch.start();
        log.info("begin convert svg to png,svgFilePath:{},pngFilePath:{}", svgFilePath, pngFilePath);
        InputStream inputStream = new FileInputStream(svgFilePath);
        OutputStream outputStream = new FileOutputStream(pngFilePath);
        try {
            PNGTranscoder pngTranscoder = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(inputStream);
            TranscoderOutput output = new TranscoderOutput(outputStream);
            pngTranscoder.transcode(input, output);
            outputStream.flush();
        } catch (Exception e) {
            log.info("convert svg to png happened error\r\n", e);
        } finally {
            stopWatch.stop();
            log.info("convert svg to png done,svgFilePath:{},cost:{}",svgFilePath,stopWatch.getTime()+"ms");
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
//        String svgFile = "/Users/liuq/Downloads/pic/4.svg";
//        String pngFilePath = "/Users/liuq/Downloads/pic/4png.png";
//        String jpgFilePath = "/Users/liuq/Downloads/pic/4png.jpg";
//        String jpgFilePathThumb = "/Users/liuq/Downloads/pic/4thumb.png";
//        File pngFile = new File(pngFilePath);
//        if (!pngFile.exists()) {
//            convertSvgToPng(svgFile, pngFilePath);
//        }
//        File jpgFile = new File(jpgFilePath);
//        if (!jpgFile.exists()) {
//            convertSvgToJpg(svgFile, jpgFilePath);
//        }
//        Thumbnails.of(pngFile).scale(1.0).toFile(new File(jpgFilePathThumb));
    }
}
