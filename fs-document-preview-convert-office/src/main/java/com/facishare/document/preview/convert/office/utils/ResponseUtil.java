package com.facishare.document.preview.convert.office.utils;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Andy
 */
public class ResponseUtil {

  public static HttpServletResponse getResponse(HttpServletResponse response) {
    response.setStatus(200);
    response.setContentType("application/octet-stream");
    response.addHeader("Content-Disposition", "attachment");
    return response;
  }
}
