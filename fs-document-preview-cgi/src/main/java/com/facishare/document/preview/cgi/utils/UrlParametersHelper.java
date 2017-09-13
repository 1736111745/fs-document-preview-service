package com.facishare.document.preview.cgi.utils;

import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Created by liuq on 2017/4/6.
 */

public class UrlParametersHelper {
  public static String safeGetRequestParameter(HttpServletRequest request, String paramName) {
    String value = getParameter(request, paramName).trim();
    return value;
  }


  private static String getParameter(HttpServletRequest request, String paramName) {
    String result = "";
    Enumeration<String> params = request.getParameterNames();
    while (params.hasMoreElements()) {
      String key = params.nextElement();
      if (paramName.equalsIgnoreCase(key)) {
        result = request.getParameter(key);
        break;
      }
    }
    return result;
  }

  public static boolean isValidPath(String path) {
    if (Strings.isNullOrEmpty(path)) {
      return false;
    }
    if (path.startsWith("N_") || path.startsWith("TN_") || path.startsWith("TG_") || path.startsWith("A_") ||
      path.startsWith("TA_") || path.startsWith("G_") || path.startsWith("F_") || path.startsWith("S_")) {
      return true;
    }
    String[] pathSplit = path.split("_");
    return pathSplit.length == 3;
  }
}
