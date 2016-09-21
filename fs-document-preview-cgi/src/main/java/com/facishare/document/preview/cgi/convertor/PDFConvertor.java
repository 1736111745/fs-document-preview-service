package com.facishare.document.preview.cgi.convertor;

import application.dcs.IPICConvertor;
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
            LOG.info("begin get IPICConvertor");
            IPICConvertor ipicConvertor = convertobj.convertor.convertPdftoPic(filePath);
            LOG.info("end get IPICConvertor");
            int resultcode = ipicConvertor.resultCode();
            if (resultcode == 0) {
                String fileName = (page1 + 1) + ".png";
                String imageFilePath = baseDir + "/" + fileName;
                LOG.info("begin get jpg,jpg folder:{}", baseDir);
                int code =ipicConvertor.convertToPNG(page1, page2, 2.0f, baseDir);
                LOG.info("end get jpg,jpg folder:{},code:{}", baseDir,code);
                ipicConvertor.close();
                File file = new File(imageFilePath);
                if (file.exists()) {
                    return FilenameUtils.getBaseName(baseDir) + "/" + fileName;
                } else {
                    return "";
                }
            } else
                return "";
        } catch (Exception e) {
            LOG.error("error info:" + e.getStackTrace());
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }
}
