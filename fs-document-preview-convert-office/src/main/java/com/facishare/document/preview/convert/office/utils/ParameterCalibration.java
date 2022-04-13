package com.facishare.document.preview.convert.office.utils;


import com.aspose.slides.PresentationFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author Andy
 */
@Component
public class ParameterCalibration {

  public  boolean isEmpty(String fileName, byte[] fileData){
    return fileName.isEmpty() || fileData.length == 0;
  }

  /**
   *
   * @param data 文件字节流
   * @param filePath 文件url地址字符串
   * @return 文件加密返回 true 文件未加密返回 false
   */
  public  boolean checkIsEncrypt(byte[] data, String filePath){
    String fileString= Arrays.toString(data);
    String fileSuffix=filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
    if (fileSuffix.equals(".xls")){
      // FileFormatInfo对象 提供用于将文件格式枚举转换为字符串或文件扩展名
      // detectFileFormat()方法，检测并返回有关存储在流中的 Excel 格式的信息
      com.aspose.cells.FileFormatInfo  isFileLock = null;
      try {
        isFileLock = com.aspose.cells.FileFormatUtil.detectFileFormat(fileString);
      } catch (Exception e) {
        return false;
      }
      // isEncrypted()判断文件是否加锁，如果加锁返回true
      return isFileLock.isEncrypted();
    }
    if (fileSuffix.equals(".doc")){
      // isEncrypted()判断文件是否加锁，如果加锁返回true
      com.aspose.words.FileFormatInfo isFileLock= null;
      try {
        isFileLock = com.aspose.words.FileFormatUtil.detectFileFormat(fileString);
      } catch (Exception e) {
       return false;
      }
      return isFileLock.isEncrypted();
    }
    if (fileSuffix.equals(".ppt")){
      // 判断文件是否加密 加密返回true
      PresentationFactory presentationFactory= new PresentationFactory();
      return presentationFactory.getPresentationInfo(fileString).isEncrypted();
    }
    return false;
  }

}
