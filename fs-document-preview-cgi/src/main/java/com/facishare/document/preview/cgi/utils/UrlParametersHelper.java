package com.facishare.document.preview.cgi.utils;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liuq on 2017/4/6.
 */
@UtilityClass
public class UrlParametersHelper {
    public String safeGetRequestParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName) == null ? "" : request.getParameter(paramName).trim();
        return value;
    }

    public  boolean isValidPath(String path) {
        if (Strings.isNullOrEmpty(path))
            return false;
        if (path.startsWith("N_") || path.startsWith("TN_") || path.startsWith("TG_")
                || path.startsWith("A_") || path.startsWith("TA_")
                || path.startsWith("G_") || path.startsWith("F_") || path.startsWith("S_")) {
            return true;
        }
        String[] pathSplit = path.split("_");
        return pathSplit.length == 3;
    }
}
