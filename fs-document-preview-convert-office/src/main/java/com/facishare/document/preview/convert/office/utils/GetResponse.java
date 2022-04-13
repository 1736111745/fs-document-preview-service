package com.facishare.document.preview.convert.office.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Andy
 */
@Component
public class GetResponse {

  public  HttpServletResponse getFalseResponse(HttpServletResponse response) {
    response.setStatus(400);
    response.setContentType("application/json");
    response.setCharacterEncoding("utf-8");
    return response;
  }

  public HttpServletResponse getTrueResponse(HttpServletResponse response){
    response.setStatus(200);
    response.setContentType("application/octet-stream");
    response.addHeader("Content-Disposition", "attachment");
    return response;
  }
}
