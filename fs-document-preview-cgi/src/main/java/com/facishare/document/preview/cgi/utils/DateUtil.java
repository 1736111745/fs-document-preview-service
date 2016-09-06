package com.facishare.document.preview.cgi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuq on 16/9/6.
 */
public class DateUtil {

    public static String getFormatDateStr(final String f) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(f);
        String yyMM = format.format(date);
        return yyMM;
    }
}
