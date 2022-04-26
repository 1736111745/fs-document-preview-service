package com.facishare.document.preview.convert.office.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Andy
 */
@Slf4j
public class ConvertDocumentUtil {

  private static byte[] toByteArray(InputStream in) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024 * 4];
    int n = 0;
    try {
      while ((n = in.read(buffer)) != -1) {
        out.write(buffer, 0, n);
      }
    } catch (IOException e) {

    }
    return out.toByteArray();
  }

  private static void deleteTempDirectory(File file) {
    File[] listFile = file.listFiles();
    if (listFile != null) {
      for (File temp : listFile) {
        deleteTempDirectory(temp);
      }
    }
    file.delete();
  }
}
