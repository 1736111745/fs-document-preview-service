package com.facishare.document.preview.cgi.convertor;

import application.dcs.Convert;
import application.dcs.IPICConvertor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by liuq on 16/9/9.
 */
public class ExcelConvertor implements IDocConvertor {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelConvertor.class);

    @Override
    public String convert(int page1, int page2,String filePath, String baseDir) throws Exception {
        Convert convert = (Convert) ConvertorPool.getInstance().borrowObject();
        try {
            LOG.info("begin get IPICConvertor");
            IPICConvertor ipicConvertor = convert.convertMStoPic(filePath);
            LOG.info("end get IPICConvertor");
            int resultcode = ipicConvertor.resultCode();
            if (resultcode == 0) {
                String fileName = (page1 + 1) + ".png";
                String imageFilePath = baseDir + "/" + fileName;
                LOG.info("begin get svg,folder:{}", baseDir);
                ipicConvertor.convertToSVG(page1, page2, 1.0f, baseDir);
                LOG.info("end get svg,folder:{}", baseDir);
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
            ConvertorPool.getInstance().returnObject(convert);
        }
    }
}