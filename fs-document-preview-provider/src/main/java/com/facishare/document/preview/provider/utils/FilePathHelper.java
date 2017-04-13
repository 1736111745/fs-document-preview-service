package com.facishare.document.preview.provider.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by liuq on 2016/12/20.
 */
public class FilePathHelper {
    public static String getFilePath(String filePath, int startPageIndex, int startIndex, String fileExt) {
        String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String fileDirPath=FilenameUtils.concat(baseDir, "p" +  (startPageIndex + startIndex));
        File fileDir=new File(fileDirPath);
        if(!fileDir.exists()) {
            fileDir.mkdir();
        }
        String fileName = (startPageIndex + startIndex) + "." + fileExt;
        return FilenameUtils.concat(baseDir, fileDirPath + "/" + fileName);
    }
}
