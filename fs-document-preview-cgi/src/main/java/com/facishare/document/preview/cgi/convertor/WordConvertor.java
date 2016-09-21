package com.facishare.document.preview.cgi.convertor;

import application.dcs.IPICConvertor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by liuq on 16/9/9.
 */
public class WordConvertor implements IDocConvertor {
    private static final Logger LOG = LoggerFactory.getLogger(WordConvertor.class);

    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            LOG.info("begin convert file:{},page:{}",filePath,page1);
            IPICConvertor ipicConvertor = convertobj.convertor.convertMStoPic(filePath);
            int resultcode = ipicConvertor.resultCode();
            LOG.info("get convertor result code:{}",resultcode);
            if (resultcode == 0) {
                String fileName = (page1 + 1) + ".svg";
                String dataFilePath = baseDir + "/" + fileName;
                LOG.info("data file name:{}",fileName);
                int code = ipicConvertor.convertToSVG(page1, page2, 1.0f, baseDir);
                LOG.info("convert page:{},result code:{}",code);
                ipicConvertor.close();
                File file = new File(dataFilePath);
                if (file.exists()) {
                    return FilenameUtils.getBaseName(baseDir) + "/" + fileName;
                } else {
                    return "";
                }
            } else
                return "";
        } catch (Exception e) {
            LOG.error("error info",e);
            return "";
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertobj);
        }
    }
}