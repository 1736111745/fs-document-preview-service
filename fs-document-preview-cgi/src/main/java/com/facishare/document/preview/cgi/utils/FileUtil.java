package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.common.model.PreviewInfo;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author liuquan
 * @date 2022/1/6  11:32 上午
 */
@UtilityClass
public class FileUtil {

  public boolean exists(PreviewInfo previewInfo) {
    return new File(previewInfo.getDataDir()).exists();
  }

  public void delete(String dir) {
    File file = new File(dir);
    if (file.exists()) {
      FileUtils.deleteQuietly(file);
    }
  }

  public void deleteEmptyDir(String dirName) {
    File dir = new File(dirName);
    File[] files = dir.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        if (file.list().length == 0) {
          file.delete();
        } else {
          deleteEmptyDir(file.getPath());
          if (file.list().length == 0) {
            file.delete();
          }
        }
      }
    }
  }

  public static void main(String[] args) {
    deleteEmptyDir("/Users/liuquan/Desktop/123");
  }
}
