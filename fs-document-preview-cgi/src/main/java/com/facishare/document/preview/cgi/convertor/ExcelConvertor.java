package com.facishare.document.preview.cgi.convertor;

import application.dcs.IHtmlConvertor;
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
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            LOG.info("begin get IPICConvertor");
            IHtmlConvertor htmlConvertor = convertobj.convertor.convertMStoHtml(filePath);
            LOG.info("end get IPICConvertor");
            int resultcode = htmlConvertor.resultCode();
            if (resultcode == 0) {
                htmlConvertor.setNormal(true);
                String fileName = (page1 + 1) + ".html";
                String htmlFilePath = baseDir + "/" + fileName;
                LOG.info("begin get html,filePath,{},folder:{}", filePath,baseDir);
                htmlConvertor.convertToHtml(htmlFilePath,page1);
                LOG.info("end get svg,folder:{}");
                htmlConvertor.close();
                File file = new File(htmlFilePath);
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