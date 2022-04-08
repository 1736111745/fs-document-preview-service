package com.facishare.document.preview.office2pdf.util;

import com.aspose.slides.PresentationFactory;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/4/1 10:19]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/1 10:19]
 * @updateRemark : [说明本次修改内容]
 */
public class FileEncryptChecker {
  /*
  * 判断文件是否加锁或加密 加锁返回true
  * */

  public static  boolean checkIsEncrypt(byte[] data, String filePath) throws Exception {
   String fileString=data.toString();
    String fileSuffix=filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
    if (fileSuffix!=null&&fileSuffix.equals(".xls")){
      // FileFormatInfo对象 提供用于将文件格式枚举转换为字符串或文件扩展名
      // detectFileFormat()方法，检测并返回有关存储在流中的 Excel 格式的信息
      com.aspose.cells.FileFormatInfo  isFileLock = com.aspose.cells.FileFormatUtil.detectFileFormat(fileString);
      // isEncrypted()判断文件是否加锁，如果加锁返回true
      return isFileLock.isEncrypted();
    }
    if (fileSuffix!=null&&fileSuffix.equals(".doc")){
      // isEncrypted()判断文件是否加锁，如果加锁返回true
      com.aspose.words.FileFormatInfo isFileLock= com.aspose.words.FileFormatUtil.detectFileFormat(fileString);
      return isFileLock.isEncrypted();
    }
    if (fileSuffix!=null&&fileSuffix.equals(".ppt")){
      // 判断文件是否加密 加密返回true
      PresentationFactory  presentationFactory= new PresentationFactory();
      boolean isFileEncryption=presentationFactory.getPresentationInfo(fileString).isEncrypted();
      return isFileEncryption;
    }
    return false;
  }

}
