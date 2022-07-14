package com.facishare.document.preview.convert.office.utils;

import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.Office2PdfException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Andy
 */
public class ResponseUtil {
  private ResponseUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  public static void setResponse(HttpServletResponse response) {
    response.setStatus(200);
    response.setContentType("application/octet-stream");
    response.addHeader("Content-Disposition", "attachment");
  }
}
