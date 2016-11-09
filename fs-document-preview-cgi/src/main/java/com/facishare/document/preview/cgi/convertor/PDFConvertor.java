package com.facishare.document.preview.cgi.convertor;

import application.dcs.IPICConvertor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Created by liuq on 16/9/9.
 */
public class PDFConvertor implements IDocConvertor {
    private static final Logger LOG = LoggerFactory.getLogger(PDFConvertor.class);

    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            IPICConvertor ipicConvertor = convertobj.convertor.convertPdftoPic(filePath);
            int resultcode = ipicConvertor.resultCode();
            if (resultcode == 0) {
                String fileName = (page1 + 1) + ".png";
                String pngFilePath = baseDir + "/" + fileName;
                ipicConvertor.close();
                File file = new File(pngFilePath);
                if (file.exists()) {
                    Thumbnails.of(file).scale(0.4).outputFormat("png").toFile(file);
                    return FilenameUtils.getBaseName(baseDir) + "/" + fileName;
                } else {
                    return "";
                }
            } else
                return "";
        } catch (Exception e) {
            LOG.error("error info", e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }

    @Override
    public String convert(int page1, int page2, String filePath, String baseDir, int width) throws Exception {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            IPICConvertor ipicConvertor = convertobj.convertor.convertPdftoPic(filePath);
            int resultcode = ipicConvertor.resultCode();
            if (resultcode == 0) {
                String fileName = (page1 + 1) + ".png";
                String pngFilePath = baseDir + "/" + fileName;
                ipicConvertor.close();
                File file = new File(pngFilePath);
                if (file.exists()) {
                    Thumbnails.of(file).width(width).outputFormat("png").toFile(file);
                    return FilenameUtils.getBaseName(baseDir) + "/" + fileName;
                } else {
                    return "";
                }
            } else
                return "";
        } catch (Exception e) {
            LOG.error("error info", e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }


    public static void main(String[] args) {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        String docPath = "/Users/liuq/DocPreviewTest/1.doc";
        String pptpath = "/Users/liuq/DocPreviewTest/2.ppt";
        String pdfPath = "/Users/liuq/DocPreviewTest/4.pdf";
        String excelPath = "/Users/liuq/DocPreviewTest/4.xlsx";
        IPICConvertor ipicConvertor = convertobj.convertor.convertMStoPic(pptpath);
        int code =ipicConvertor.convertToPNG(0, 0, 2f, "/Users/liuq/DocPreviewTest/Result");
        ipicConvertor.convertToSVG(0, 0, 2f, "/Users/liuq/DocPreviewTest/Result");
    }
}
