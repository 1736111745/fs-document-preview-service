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
    public String convert(int page1, int page2,String filePath, String baseDir,int exceptWidth) throws Exception {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            LOG.info("begin get IPICConvertor");
            IPICConvertor ipicConvertor = convertobj.convertor.convertMStoPic(filePath);
            LOG.info("end get IPICConvertor");
            int resultcode = ipicConvertor.resultCode();
            if (resultcode == 0) {
                String fileName = (page1 + 1) + ".jpg";
                String imageFilePath = baseDir + "/" + fileName;
                LOG.info("begin get jpg,folder:{}", baseDir);
                float width=ipicConvertor.getAllpageWHeigths()[page1][0];
                float scale=exceptWidth/width;
                int code=ipicConvertor.convertToJPG(page1, page2, scale, baseDir);
                LOG.info("end get jpg,folder:{},code:{}", baseDir,code);
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