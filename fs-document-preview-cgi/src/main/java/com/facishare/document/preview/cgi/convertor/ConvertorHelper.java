package com.facishare.document.preview.cgi.convertor;

import application.dcs.IHtmlConvertor;
import application.dcs.IPICConvertor;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuq on 2016/11/9.
 */
public class ConvertorHelper {
    private static final Logger logger = LoggerFactory.getLogger(ConvertorHelper.class);

    public static String toSvg(int page1, int page2, String filePath, String baseDir) {
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
            logger.error("toSvg,filepath:{}", filePath, e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }

    public static String toPng(int page1, int page2, String filePath, String baseDir, int startIndex, int type) {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            IPICConvertor ipicConvertor = type == 1 ?
                    convertobj.convertor.convertMStoPic(filePath) :
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
            logger.error("toPng,filepath:{}", filePath, e);
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
            logger.error("toHtml,filepath:{}", filePath, e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }

    public static void main(String[] args) throws IOException {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        String docPath = "/Users/liuq/DocPreviewTest/1.doc";
        String pptpath = "/Users/liuq/DocPreviewTest/2.ppt";
        String pdfPath = "/Users/liuq/Ebook/Intellij IDEA教程.pdf";
        String excelPath = "/Users/liuq/DocPreviewTest/4.xlsx";
        IPICConvertor ipicConvertor = convertobj.convertor.convertPdftoPic(pdfPath);
        int code = ipicConvertor.convertToPNG(2, 2, 2f, "/Users/liuq/DocPreviewTest/Result");
        Thumbnails.of("/Users/liuq/DocPreviewTest/Result/3.png").width(1080).outputQuality(0.8).toFile("/Users/liuq/DocPreviewTest/Result/a.png");
//        Thumbnails.of("/Users/liuq/Pictures/刘晨曦/底片/004.jpg").width(1024).outputQuality(0.7).toFile("/Users/liuq/DocPreviewTest/Result/a1.png");
    }
}
