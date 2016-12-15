package com.facishare.document.preview.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuq on 16/9/6.
 */
public class DateUtil {

    public static String getFormatDate(final String f) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(f);
        String yyMM = format.format(date);
        return yyMM;
    }

    public static int getFormatDateInt(final String f) {
        return Integer.parseInt(getFormatDate(f));
    }
}
