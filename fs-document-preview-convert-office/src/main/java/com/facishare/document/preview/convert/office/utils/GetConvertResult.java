package com.facishare.document.preview.convert.office.utils;

import com.facishare.document.preview.common.model.ConvertResult;
import org.springframework.stereotype.Component;

/**
 * @author Andy
 */
@Component
public class GetConvertResult {

  public  ConvertResult getFalseConvertResult(String errorMsg){
    ConvertResult convertResult=new ConvertResult();
    convertResult.setSuccess(false);
    convertResult.setErrorMsg(errorMsg);
    return convertResult;
  }
  public  ConvertResult getTrueConvertResult(){
    ConvertResult convertResult=new ConvertResult();
    convertResult.setSuccess(true);
    convertResult.setErrorMsg("");
    return convertResult;
  }
}
