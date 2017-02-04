package com.facishare.document.preview.common.utils;

import com.aspose.cells.FileFormatInfo;
import com.aspose.cells.FileFormatUtil;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by liuq on 2017/2/4.
 */
public class OfficeFileEncryptChecker {

    public static boolean check(String filePath) {
        try {
            FileFormatInfo info = FileFormatUtil.detectFileFormat(filePath);
            return info.isEncrypted();
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        String rootDir = System.getenv("HOME") + "/Downloads/";
        File root = new File(rootDir);
        FileFilter fileFilter = pathname -> pathname.getName().toLowerCase().endsWith(".pptx");
        File[] files=root.listFiles(fileFilter);
        for (File file : files) {
            long start = System.currentTimeMillis();
            boolean flag = check(file.getPath());
            System.out.println("========="+file.getName()+"加密状态:"+flag+"=======");
            System.out.println("cost: " + (System.currentTimeMillis() - start) + "ms\n\n");
        }
    }
}
