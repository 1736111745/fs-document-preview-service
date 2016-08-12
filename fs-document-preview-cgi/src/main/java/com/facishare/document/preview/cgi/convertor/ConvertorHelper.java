package com.facishare.document.preview.cgi.convertor;

import application.dcs.Convert;
import com.github.autoconf.spring.reloadable.ReloadableProperty;

/**
 * Created by liuq on 16/8/8.
 */
public class ConvertorHelper {
    @ReloadableProperty("temp-dir")
    private static String tempDir="";
    public static int doConvert(String in, String out) {
        ConvertorObject convertorObject = getConvertor();
        Convert convertor = convertorObject.getConvertor();
        convertor.setAcceptTracks(true);
        convertor.setConvertForPhone(true);
        convertor.setHtmlEncoding("UTF-8");
        convertor.setTempPath(tempDir);
        int code = in.toLowerCase().contains(".pdf") ? convertor.convertPdfToHtml(in, out) : convertor.convertMStoHtmlOfSvg(in, out);
        convertor.deleteTempFiles();
        ConvertorPool.getInstance().releaseConvertor(convertorObject);
        return code;
    }

    private static ConvertorObject getConvertor() {
        ConvertorObject convertorObject = ConvertorPool.getInstance().getConvertor();
        convertorObject.getConvertor();
        return convertorObject;
    }
}
