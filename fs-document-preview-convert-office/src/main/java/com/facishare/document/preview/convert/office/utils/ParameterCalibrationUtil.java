package com.facishare.document.preview.convert.office.utils;

import cn.hutool.core.io.file.FileNameUtil;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.google.common.base.Strings;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

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

  public static FileTypeEnum getFileType(String filePath) throws Office2PdfException {
    try {
      return FileTypeEnum.valueOf(FileNameUtil.extName(filePath).toUpperCase());
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH, e);
    }
  }

  public static FileTypeEnum isExcelType(String filePath) throws Office2PdfException {
    try {
     FileTypeEnum fileTypeEnum= FileTypeEnum.valueOf(FileNameUtil.extName(filePath).toUpperCase());
      if ((fileTypeEnum.compareTo(FileTypeEnum.XLS)==0)||(fileTypeEnum.compareTo(FileTypeEnum.XLSX)==0)){
        return fileTypeEnum;
      }else {
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_EXCEL);
      }
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH, e);
    }
  }

  public static int isZero(int page) throws Office2PdfException {
    if (page == 0) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, page);
    }
    return page;
  }

  public static byte[] isNullOrEmpty(String path, MultipartFile file) throws Office2PdfException {
    if (Strings.isNullOrEmpty(path)) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_PATH_EMPTY);
    }
    try {
      byte[] fileBytes = file.getBytes();
      if (fileBytes.length <=50) {
        throw new Office2PdfException(ErrorInfoEnum.EMPTY_FILE);
      }
      return fileBytes;
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_STREAM_ERROR);
    }
  }
}
