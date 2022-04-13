package com.facishare.document.preview.convert.office.utils;

import com.facishare.document.preview.convert.office.model.ConvertResultInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;

/**
 * @author Andy
 */
@Component
public class GetConvertResultInfo {

  @Resource
  private ConvertResultInfo convertResultInfo;

  public ConvertResultInfo getTrueConvertResultInfo(ByteArrayOutputStream fileOutputStream){
    convertResultInfo.setErrorMsg("");
    convertResultInfo.setBytes(fileOutputStream.toByteArray());
    convertResultInfo.setSuccess(true);
    return convertResultInfo;
  }

  public ConvertResultInfo getTrueConvertResultInfo(byte[] data){
    convertResultInfo.setErrorMsg("格式未能转换成功，请检查文档是否加锁、加密或损坏");
    convertResultInfo.setBytes(data);
    convertResultInfo.setSuccess(true);
    return convertResultInfo;
  }

  public ConvertResultInfo getFalseConvertResultInfo(String errorMsg){
    convertResultInfo.setErrorMsg(errorMsg);
    convertResultInfo.setSuccess(false);
    return convertResultInfo;
  }

}
