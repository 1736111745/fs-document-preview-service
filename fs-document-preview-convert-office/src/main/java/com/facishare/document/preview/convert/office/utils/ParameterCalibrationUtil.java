package com.facishare.document.preview.convert.office.utils;

import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.google.common.base.Strings;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [参数校验工具类]
 * @createTime : [2022/4/21 10:28]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/21 10:28]
 * @updateRemark : [精简代码，进一步封装]
 */
public class ParameterCalibrationUtil {

  public static FileTypeEnum getFileType(String filePath, byte[] fileBate) throws Office2PdfException {
    String fileTypeName = FileProcessingUtil.getFileType(fileBate);
    if (isFormatSupport(fileTypeName)) {
      return FileTypeEnum.valueOf(FileProcessingUtil.extName(filePath).toUpperCase());
    }
    throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
  }

  public static boolean isFormatSupport(String fileTypeName) {
    return FileTypeEnum.getFileTypeName(fileTypeName) != null;
  }

  public static void isDifference(byte[] fileBate) throws Office2PdfException {
    String fileTypeName = FileProcessingUtil.getFileType(fileBate);
    if (!isFormatSupport(fileTypeName)) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }

  public static byte[] isNullOrEmpty(String path, MultipartFile file) throws Office2PdfException {
    if (Strings.isNullOrEmpty(path)) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_PATH_EMPTY);
    }
    byte[] fileBate;
    try {
      fileBate = file.getBytes();
      if (fileBate.length <= 0) {
        throw new Office2PdfException(ErrorInfoEnum.EMPTY_FILE);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_PARAMETER_ERROR);
    }
    return fileBate;
  }

  public static byte[] isNullOrEmpty(String path, MultipartFile file, int page) throws Office2PdfException {
    if (page == 0) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, new Office2PdfException());
    }
    if (Strings.isNullOrEmpty(path)) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_PATH_EMPTY);
    }
    byte[] fileBate;
    try {
      fileBate = file.getBytes();
      if (fileBate.length <= 0) {
        throw new Office2PdfException(ErrorInfoEnum.EMPTY_FILE);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_PARAMETER_ERROR);
    }
    return fileBate;
  }
}
