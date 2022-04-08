package com.facishare.document.preview.office2pdf.util;

import com.facishare.document.preview.common.model.ConvertResult;

import javax.servlet.http.HttpServletResponse;

public class ConvertResultHelper {

  public  static ConvertResult getFalseConverResult(String errorMsg){
    ConvertResult convertResult=new ConvertResult();
    convertResult.setSuccess(false);
    convertResult.setErrorMsg(errorMsg);
    return convertResult;
  }

  public  static ConvertResult getTrueConverResult(String errorMsg){
    ConvertResult convertResult=new ConvertResult();
    convertResult.setSuccess(false);
    convertResult.setErrorMsg(errorMsg);
    return convertResult;
  }

  public static HttpServletResponse getFalseResponse(){
    HttpServletResponse response = null;
    response.setStatus(400);
    response.setContentType("application/json");
    response.setCharacterEncoding("utf-8");
    return response;
  }
  public static HttpServletResponse getTrueResponse(){
    HttpServletResponse response = null;
    response.setStatus(200);
    response.setContentType("application/octet-stream");
    response.addHeader("Content-Disposition", "attachment");
    return response;
  }
}
