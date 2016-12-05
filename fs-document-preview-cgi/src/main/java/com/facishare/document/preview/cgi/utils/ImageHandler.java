package com.facishare.document.preview.cgi.utils;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;

import java.io.*;

/**
 * Created by liuq on 16/9/22.
 */
public class ImageHandler {
    public static void convertSvgToJpg(String svgFilePath, String jpgFilePath)
            throws TranscoderException, IOException {
        InputStream inputStream = new FileInputStream(svgFilePath);
        OutputStream outputStream=new FileOutputStream(jpgFilePath);
        try {
            JPEGTranscoder t = new JPEGTranscoder();
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
        String svgFile = "/Users/liuq/Downloads/1.svg";
        String jpgFilePath ="/Users/liuq/Downloads/abcddd.jpg";
        File jpgFile=new File(jpgFilePath);
        if(!jpgFile.exists())
        {
            convertSvgToJpg(svgFile,jpgFilePath);
        }

    }
}
