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
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/4/21 10:28]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/21 10:28]
 * @updateRemark : [说明本次修改内容]
 */
public class ParameterCalibrationUtil {

  public static FileTypeEnum getFileType(String filePath, byte[] fileBate) throws Office2PdfException {
    String fileTypeName = OfficeFileTypeUtil.getFileType(fileBate);
    if (isFormatSupport(fileTypeName)) {
      return FileTypeEnum.valueOf(OfficeFileTypeUtil.extName(filePath).toUpperCase());
    }
    throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
  }

  public static boolean isDifference(String filePath, byte[] fileBate) throws Office2PdfException {
    String fileTypeName = OfficeFileTypeUtil.getFileType(fileBate);
    if (isFormatSupport(fileTypeName)) {
      return true;
    }
    throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
  }

  public static boolean isFormatSupport(String fileTypeName) {
    if (FileTypeEnum.getFileTypeName(fileTypeName) == null) {
      return false;
    }
    return true;
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
}
