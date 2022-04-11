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
    //创建转换结果集信息对象
    ConverResultInfo converResultInfo = new ConverResultInfo();
    //创建文件对象
    File sourceFile = new File(sourFilePath);
    //创建文件输出流对象
    FileOutputStream fileOutputStream = null;
    //创建压缩文件输出流对象
    ZipOutputStream zipOutputStream = null;
    //创建压缩包实体对象
    ZipEntry zipEntry = null;
    //创建字节数组对象
    byte[] date = new byte[1024 * 10];
    //判断目标文件夹是否存在
    if (!sourceFile.exists()) {
      //如果为空，报错并将异常携带出
      converResultInfo.setSuccess(false);
      converResultInfo.setErrorMsg("源文件夹目录不存在");
      return converResultInfo;
    } else {
      //成功则将目标文件夹内文件读入File对象数组
      File[] sourceFiles = sourceFile.listFiles();
      //判断目标文件夹内是否有文件
      if (sourceFiles.length < 1) {
        //如果目标文件夹内没有文件则报错并将异常信息带出
        converResultInfo.setSuccess(false);
        converResultInfo.setErrorMsg("目标文件夹下不存在文件");
        return converResultInfo;
      } else {
        try {
          //如果文件存在，则创建文件输出流对象（输出为压缩包）
          fileOutputStream = new FileOutputStream(zipFilePath);
          zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));

          //开始循环读入File对象数组（也就是图片）
          for (int i = 0; i < sourceFiles.length; i++) {
            //将File对象数组中的一张图片压缩为压缩包对象
            zipEntry = new ZipEntry(sourceFiles[i].getName());
            //将压缩包对象读入压缩输出流
            zipOutputStream.putNextEntry(zipEntry);
            //创建文件输入流对象，并将对应图片读入文件输入流对象
            FileInputStream fileInputStream = new FileInputStream(sourceFiles[i]);
            //创建文件缓冲输入流，在将文件输入流对象读入的同时，指定缓冲流大小
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 1024 * 10);

            int read = 0;
            //将缓冲流对象内的数据（图片）写入压缩包输出流
            while ((read = bufferedInputStream.read(date, 0, 1024 * 10)) != -1) {
              zipOutputStream.write(date, 0, read);
            }
          }
          //捕捉文件创建异常，并将异常信息写入转换结果集信息对象，以便带出
        } catch (FileNotFoundException e) {
          converResultInfo.setSuccess(false);
          converResultInfo.setErrorMsg(e.toString());
          return converResultInfo;
          //捕捉IO流创建异常，并将异常信息写入转换结果集信息对象，以便带出
        } catch (IOException e) {
          converResultInfo.setSuccess(false);
          converResultInfo.setErrorMsg(e.toString());
          return converResultInfo;
          //关闭各种输入输出流
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
            return converResultInfo;
          }
        }
      }
    }
    //如果成功，将数据与成功信息带出
    converResultInfo.setSuccess(true);
    converResultInfo.setBytes(zipEntry.getExtra());
    return converResultInfo;
  }

}
