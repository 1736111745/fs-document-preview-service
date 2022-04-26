package com.facishare.document.preview.convert.office.utils;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/4/21 10:30]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/21 10:30]
 * @updateRemark : [说明本次修改内容]
 */
public class OfficeFileTypeUtil {

  public static String getFileType(byte[] fileBate) throws Office2PdfException {
    try (ByteArrayInputStream data = new ByteArrayInputStream(fileBate, 0, 100)) {
      return FileTypeUtil.getType(data);
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.STREAM_CLOSING_ANOMALY, e);
    }
  }

  public static String extName(String fileName) {
    return FileNameUtil.extName(fileName);
  }

}
