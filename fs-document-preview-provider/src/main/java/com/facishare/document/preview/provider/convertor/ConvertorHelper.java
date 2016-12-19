package com.facishare.document.preview.provider.convertor;

import application.dcs.IHtmlConvertor;
import application.dcs.IPICConvertor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.File;


/**
 * Created by liuq on 2016/11/9.
 */
@Slf4j
public class ConvertorHelper {
    public ConvertorHelper() throws Exception {
    }

    public static String toSvg(int page1, int page2, String filePath, String baseDir) {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            IPICConvertor ipicConvertor = convertobj.convertor.convertMStoPic(filePath);
            if (ipicConvertor != null) {
                int resultCode = ipicConvertor.resultCode();
                if (resultCode == 0) {
                    String fileName = (page1 + 1) + ".svg";
                    String svgFilePath = FilenameUtils.concat(baseDir, fileName);
                    ipicConvertor.convertToSVG(page1, page2, 1.0f, baseDir);
                    ipicConvertor.close();
                    File file = new File(svgFilePath);
                    if (file.exists()) {
                        return svgFilePath;
                    } else {
                        return "";
                    }
                } else {
                    log.warn("filePath:{},resultCode:{}", filePath, resultCode);
                    return "";
                }
            } else {
                log.warn("converter is null");
                return "";
            }
        } catch (Exception e) {
            log.error("toSvg,filepath:{}", filePath, e);
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
            int resultCode = ipicConvertor.resultCode();
            if (resultCode == 0) {
                String fileName = (page1 + startIndex) + ".png";
                String pngFilePath = baseDir + "/" + fileName;
                ipicConvertor.convertToPNG(page1, page2, 1f, baseDir);
                ipicConvertor.close();
                File file = new File(pngFilePath);
                if (file.exists()) {
                    return pngFilePath;
                } else {
                    return "";
                }
            } else {
                log.warn("filePath:{},pageIndex:{},resultCode:{}", filePath, page1, resultCode);
                return "";
            }
        } catch (Exception e) {
            log.error("toPng,filepath:{}", filePath, e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }

    public static String toHtml(int page1, String filePath, String baseDir) {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            IHtmlConvertor htmlConvertor = convertobj.convertor.convertMStoHtml(filePath);
            int resultCode = htmlConvertor.resultCode();
            if (resultCode == 0) {
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
            } else {
                log.warn("resultcode:{}", resultCode);
                return "";
            }
        } catch (Exception e) {
            log.error("toHtml,filepath:{}", filePath, e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }

    public static void main(String[] args) throws Exception {

    }
}
