package com.facishare.document.preview.cgi.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liuq on 2016/12/17.
 */
public class RequestParamsHelper {

    public static String safteGetRequestParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName) == null ? "" : request.getParameter(paramName).trim();
        return value;
    }
}
