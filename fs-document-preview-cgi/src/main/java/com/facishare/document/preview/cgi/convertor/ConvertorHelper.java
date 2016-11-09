package com.facishare.document.preview.cgi.convertor;

import application.dcs.IHtmlConvertor;
import application.dcs.IPICConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by liuq on 2016/11/9.
 */
public class ConvertorHelper {
    private static final Logger logger = LoggerFactory.getLogger(ConvertorHelper.class);
    public static String toSvg(int page1, int page2,String filePath, String baseDir)
    {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            IPICConvertor ipicConvertor = convertobj.convertor.convertMStoPic(filePath);
            int resultcode = ipicConvertor.resultCode();
            if (resultcode == 0) {
                String fileName = (page1 + 1) + ".svg";
                String svgFilePath = baseDir + "/" + fileName;
                ipicConvertor.convertToSVG(page1, page2, 1.0f, baseDir);
                ipicConvertor.close();
                File file = new File(svgFilePath);
                if (file.exists()) {
                    return svgFilePath;
                } else {
                    return "";
                }
            } else
                return "";
        } catch (Exception e) {
            logger.error("toSvg",e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }

    public static String toPng(int page1, int page2,String filePath, String baseDir,int startIndex,int type) {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            IPICConvertor ipicConvertor = type==1?
                    convertobj.convertor.convertMStoPic(filePath):
                    convertobj.convertor.convertPdftoPic(filePath);
            int resultcode = ipicConvertor.resultCode();
            if (resultcode == 0) {
                String fileName = (page1 + startIndex) + ".png";
                String pngFilePath = baseDir + "/" + fileName;
                ipicConvertor.convertToPNG(page1, page2, 2f, baseDir);
                ipicConvertor.close();
                File file = new File(pngFilePath);
                if (file.exists()) {
                    return pngFilePath;
                } else {
                    return "";
                }
            } else
                return "";
        } catch (Exception e) {
            logger.error("toPng", e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }



    public static String toHtml(int page1, String filePath, String baseDir) {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            IHtmlConvertor htmlConvertor = convertobj.convertor.convertMStoHtml(filePath);
            int resultcode = htmlConvertor.resultCode();
            if (resultcode == 0) {
                htmlConvertor.setNormal(true);
                String fileName = (page1 + 1) + ".html";
                String htmlFilePath = baseDir + "/" + fileName;
                htmlConvertor.convertToHtml(htmlFilePath, page1);
                htmlConvertor.close();
                File file = new File(htmlFilePath);
                if (file.exists()) {
                    return htmlFilePath;
                } else {
                    return "";
                }
            } else
                return "";
        } catch (Exception e) {
            logger.error("toHtml", e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }
}
