package com.facishare.document.preview.cgi.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by liuq on 2016/11/14.
 */
public class WebpHelper {
    public static boolean convertToWebp(String inputFile, String outputFile) {
        return convertToWebp(inputFile, outputFile, 75);
    }

    public static boolean convertToWebp(String inputFile, String outputFile, Integer quality) {
        if (!new File(inputFile).exists()) {
            return false;
        }

        String outputPath = FilenameUtils.getFullPath(outputFile);
        if (!new File(outputPath).exists()) {
            new File(outputPath).mkdirs();
        }

        return executeCWebp(inputFile, outputFile, quality);
    }

    /**
     * execute cwebp command：cwebp [options] input_file -o output_file.webp
     */
    private static boolean executeCWebp(String inputFile, String outputFile, Integer quality) {
        boolean result = false;
        ClassLoader cl = WebpHelper.class.getClassLoader(); // get classloader
        // init cwebp path，and set privilege of 755.
        // you can replace cwebpath in your case. in this case, we used a macos-based cwebp
        String cwebpPath = cl.getResource("libwebp/macos/bin/cwebp").getPath();
        try {
            String chmodCommand = "chmod 755 " + cwebpPath;
            Runtime.getRuntime().exec(chmodCommand).waitFor();

            StringBuilder command = new StringBuilder(cwebpPath);
            command.append(" -q " + (quality == 0 ? 75 : quality));
            command.append(" -resize 800 600");
            command.append(" " + inputFile);
            command.append(" -o " + outputFile);

            Runtime.getRuntime().exec(command.toString());

            result = true;
        } catch (Exception e) {
            // log.error("An error happend when convert to webp. Img is: " + inputFile, e);
        }
        return result;
    }
    public static void main(String[] args) {
        String base="/share/docconvert/normal/dps/201611/14/19/7/fxivm70n";
        File folder = new File(base);
        String[] files = folder.list();
        for (int i = 0; i < files.length; i++) {
            String inputFile = base+"/" +files[i];
            String outputFile = base+"/" +(i+1) +".webp";
            if (executeCWebp(inputFile, outputFile, 75)) {
                System.out.println("convert ok~");
            } else {
                System.out.println("sth wrong happened");
            }
        }
    }
}
