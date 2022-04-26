package com.facishare.document.preview.convert.office.utils;

import com.facishare.document.preview.convert.office.model.ConvertResultInfo;

/**
 * @author Andy
 */
public class ConvertResultInfoUtil {

  public static ConvertResultInfo getConvertResultInfo(byte[] fileBate) {
    ConvertResultInfo convertResultInfo = new ConvertResultInfo();
    convertResultInfo.setErrorMsg("");
    convertResultInfo.setBytes(fileBate);
    convertResultInfo.setSuccess(true);
    return convertResultInfo;
  }

  public static ConvertResultInfo getTrueConvertResultInfo(byte[] data) {
    ConvertResultInfo convertResultInfo = new ConvertResultInfo();
    convertResultInfo.setErrorMsg("格式未能转换成功，请检查文档是否加锁、加密或损坏");
    convertResultInfo.setBytes(data);
    convertResultInfo.setSuccess(true);
    return convertResultInfo;
  }

  public static ConvertResultInfo getConvertResultInfo(String errorMsg) {
    ConvertResultInfo convertResultInfo = new ConvertResultInfo();
    convertResultInfo.setErrorMsg(errorMsg);
    convertResultInfo.setSuccess(false);
    return convertResultInfo;
  }

}
