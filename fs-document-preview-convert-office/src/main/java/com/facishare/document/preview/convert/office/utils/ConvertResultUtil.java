package com.facishare.document.preview.convert.office.utils;

import com.facishare.document.preview.common.model.ConvertResult;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;

/**
 * @author Andy
 */
public class ConvertResultUtil {

  public static ConvertResult getConvertResult(String errorMsg) {
    ConvertResult convertResult = new ConvertResult();
    convertResult.setSuccess(false);
    convertResult.setErrorMsg(errorMsg);
    return convertResult;
  }

  public static ConvertResult getConvertResult(ErrorInfoEnum errorInfoEnum) {
    ConvertResult convertResult = new ConvertResult();
    convertResult.setSuccess(false);
    convertResult.setErrorMsg(errorInfoEnum.getErrorMsg());
    return convertResult;
  }
}
