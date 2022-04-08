package com.facishare.document.preview.office2pdf.util;

import com.facishare.document.preview.office2pdf.model.ConverResultInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Andy
 */
public class ConvertZip2FIle {

  /**
   * @param sourFilePath 图片源文件夹路径
   * @param zipFilePath  压缩包存放文件夹路径
   */
  public static ConverResultInfo file2Zip(String sourFilePath, String zipFilePath) {
    ConverResultInfo converResultInfo = new ConverResultInfo();
    File sourceFile = new File(sourFilePath);
    FileOutputStream fileOutputStream = null;
    ZipOutputStream zipOutputStream = null;
    ZipEntry zipEntry = null;
    byte[] date = new byte[1024 * 10];
    if (!sourceFile.exists()) {
      converResultInfo.setSuccess(false);
      converResultInfo.setErrorMsg("源文件夹目录不存在");
    } else {
      File[] sourceFiles = sourceFile.listFiles();
      if (sourceFiles.length < 1) {
        converResultInfo.setSuccess(false);
        converResultInfo.setErrorMsg("目标文件夹下不存在文件");
      } else {
        try {
          fileOutputStream = new FileOutputStream(zipFilePath);
          zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
          for (int i = 0; i < sourceFiles.length; i++) {
            zipEntry = new ZipEntry(sourceFiles[i].getName());
            zipOutputStream.putNextEntry(zipEntry);
            FileInputStream fileInputStream = new FileInputStream(sourceFiles[i]);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 1024 * 10);
            int read = 0;
            while ((read = bufferedInputStream.read(date, 0, 1024 * 10)) != -1) {
              zipOutputStream.write(date, 0, read);
            }
          }
        } catch (FileNotFoundException e) {
          converResultInfo.setSuccess(false);
          converResultInfo.setErrorMsg(e.toString());
        } catch (IOException e) {
          converResultInfo.setSuccess(false);
          converResultInfo.setErrorMsg(e.toString());
        } finally {
          try {
            if (fileOutputStream != null) {
              fileOutputStream.close();
            }
            if (zipOutputStream != null) {
              zipOutputStream.close();
            }
          } catch (IOException e) {
            converResultInfo.setSuccess(false);
            converResultInfo.setErrorMsg(e.toString());
          }
        }
      }
    }
    converResultInfo.setSuccess(true);
    converResultInfo.setBytes(zipEntry.getExtra());
    return converResultInfo;
  }

}
