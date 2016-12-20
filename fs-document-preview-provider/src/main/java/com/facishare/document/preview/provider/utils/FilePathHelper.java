package com.facishare.document.preview.provider.utils;

import org.apache.commons.io.FilenameUtils;

/**
 * Created by liuq on 2016/12/20.
 */
public class FilePathHelper {
  public static String getFilePath(String filePath,int startPageIndex,int startIndex,String fileExt) {
      String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
      String fileName = (startPageIndex + startIndex) + "." + fileExt;
      return FilenameUtils.concat(baseDir, fileName);
  }
}