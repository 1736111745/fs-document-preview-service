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
public class PPTConvertor implements IDocConvertor {
    private static final Logger LOG = LoggerFactory.getLogger(PPTConvertor.class);

    @Override
    public String convert(int page1, int page2,String filePath, String baseDir) throws Exception {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            LOG.info("begin get IPICConvertor");
            IPICConvertor ipicConvertor = convertobj.convertor.convertMStoPic(filePath);
            LOG.info("end get IPICConvertor");
            int resultcode = ipicConvertor.resultCode();
            if (resultcode == 0) {
                String fileName = (page1 + 1) + ".svg";
                String imageFilePath = baseDir + "/" + fileName;
                LOG.info("begin get svg,folder:{}", baseDir);
                float width=ipicConvertor.getAllpageWHeigths()[page1][0];
                int code=ipicConvertor.convertToSVG(page1, page2, 1.0f, baseDir);
                LOG.info("end get svg,folder:{},code:{}", baseDir,code);
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

    @Override
    public String convert(int page1, int page2, String filePath, String baseDir, int width) throws Exception {
        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
        try {
            LOG.info("begin get IPICConvertor");
            IPICConvertor ipicConvertor = convertobj.convertor.convertMStoPic(filePath);
            LOG.info("end get IPICConvertor");
            int resultcode = ipicConvertor.resultCode();
            if (resultcode == 0) {
                String fileName = (page1 + 1) + ".png";
                String pngFilePath = baseDir + "/" + fileName;
                LOG.info("begin get image,folder:{},page:{}", baseDir, page1);
                int code = ipicConvertor.convertToPNG(page1, page2, 2f, baseDir);
                LOG.info("end get image,folder:{},code:{},page:{}", baseDir, code, page1);
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
}