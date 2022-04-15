package com.facishare.document.preview.convert.office.utils;

import com.facishare.document.preview.convert.office.model.ConvertResultInfo;

import java.io.ByteArrayOutputStream;

/**
 * @author Andy
 */
public class GetConvertResultInfo {

  public ConvertResultInfo getTrueConvertResultInfo(ByteArrayOutputStream fileOutputStream){
    ConvertResultInfo convertResultInfo=new ConvertResultInfo();
    convertResultInfo.setErrorMsg("");
    convertResultInfo.setBytes(fileOutputStream.toByteArray());
    convertResultInfo.setSuccess(true);
    return convertResultInfo;
  }

  public ConvertResultInfo getTrueConvertResultInfo(byte[] data){
    ConvertResultInfo convertResultInfo=new ConvertResultInfo();
    convertResultInfo.setErrorMsg("格式未能转换成功，请检查文档是否加锁、加密或损坏");
    convertResultInfo.setBytes(data);
    convertResultInfo.setSuccess(true);
    return convertResultInfo;
  }

  public ConvertResultInfo getFalseConvertResultInfo(String errorMsg){
    ConvertResultInfo convertResultInfo=new ConvertResultInfo();
    convertResultInfo.setErrorMsg(errorMsg);
    convertResultInfo.setSuccess(false);
    return convertResultInfo;
  }

}
