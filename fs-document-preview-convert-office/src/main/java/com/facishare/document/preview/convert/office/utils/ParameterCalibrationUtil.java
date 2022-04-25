package com.facishare.document.preview.convert.office.utils;

import com.facishare.document.preview.convert.office.constant.FileTypeEnum;

import java.io.ByteArrayInputStream;

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

  public static FileTypeEnum isDifference(String filePath, ByteArrayInputStream fileStream) {
    String fileTypeName = OfficeFileTypeUtil.getFileType(fileStream);
    fileStream.reset();
    if (isFormatSupport(fileTypeName)) {
      return FileTypeEnum.valueOf(OfficeFileTypeUtil.extName(filePath).toUpperCase());
    }
    return FileTypeEnum.valueOf("zip".toUpperCase());
  }

  public static boolean isFormatSupport(String fileTypeName) {
    if (FileTypeEnum.getFileTypeName(fileTypeName) == null) {
      return false;
    }
    return true;
  }


}
