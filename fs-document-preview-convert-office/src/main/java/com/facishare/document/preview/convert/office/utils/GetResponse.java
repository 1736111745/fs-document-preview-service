package com.facishare.document.preview.convert.office.utils;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Andy
 */
public class GetResponse {

  public  static HttpServletResponse getFalseResponse(HttpServletResponse response) {
    response.setStatus(400);
    response.setContentType("application/json");
    response.setCharacterEncoding("utf-8");
    return response;
  }

  public static HttpServletResponse getTrueResponse(HttpServletResponse response){
    response.setStatus(200);
    response.setContentType("application/octet-stream");
    response.addHeader("Content-Disposition", "attachment");
    return response;
  }
}
