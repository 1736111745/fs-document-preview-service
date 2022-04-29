package com.facishare.document.preview.convert.office.utils;

import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.google.common.base.Strings;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.facishare.document.preview.convert.office.constant.FileTypeEnum.ZIP;

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

  private ParameterCalibrationUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  public static FileTypeEnum getFileType(String filePath, byte[] fileBate) throws Office2PdfException {
    FileTypeEnum fileTypeName = FileTypeEnum.valueOf(FileProcessingUtil.getFileType(fileBate).toUpperCase());
    FileTypeEnum fileName = FileTypeEnum.valueOf(FileProcessingUtil.extName(filePath).toUpperCase());
    if (isFormatSupport(fileTypeName) && !fileName.equals(ZIP)) {
      return fileName;
    }
    throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
  }

  public static boolean isFormatSupport(FileTypeEnum fileTypeName) {
    return FileTypeEnum.getFileTypeName(fileTypeName) != null;
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
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR);
    }
    return isNullOrEmpty(path, file);
  }
}
