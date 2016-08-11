package com.facishare.document.preview.cgi.convertor;

import application.dcs.Convert;

/**
 * Created by liuq on 16/8/8.
 */
public class ConvertorHelper {

    public static  int  doConvert(String in,String out) {
        ConvertorObject convertorObject = getConvertor();
        Convert convertor = convertorObject.getConvertor();
        int code = in.toLowerCase().contains(".pdf")?convertor.convertPdfToHtml(in,out):convertor.convertMStoHtmlOfSvg(in, out);
        convertorObject.getConvertor().deleteTempFiles();
        ConvertorPool.getInstance().releaseConvertor(convertorObject);
        return code;
    }

    private static ConvertorObject getConvertor() {
        ConvertorObject convertorObject = ConvertorPool.getInstance().getConvertor();
        convertorObject.getConvertor();
        return convertorObject;
    }

    private static void closeConvertor(ConvertorObject convertorObject) {
        convertorObject.getConvertor().deleteTempFiles();
        ConvertorPool.getInstance().releaseConvertor(convertorObject);
    }
}
