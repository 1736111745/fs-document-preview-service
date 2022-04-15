package com.facishare.document.preview.convert.office.utils;

import com.facishare.document.preview.common.model.ConvertResult;

/**
 * @author Andy
 */
public class GetConvertResult {

  public  static ConvertResult getFalseConvertResult(String errorMsg){
    ConvertResult convertResult=new ConvertResult();
    convertResult.setSuccess(false);
    convertResult.setErrorMsg(errorMsg);
    return convertResult;
  }
  public static   ConvertResult getTrueConvertResult(){
    ConvertResult convertResult=new ConvertResult();
    convertResult.setSuccess(true);
    convertResult.setErrorMsg("");
    return convertResult;
  }
}
