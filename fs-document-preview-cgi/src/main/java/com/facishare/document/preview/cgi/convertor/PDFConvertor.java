package com.facishare.document.preview.cgi.convertor;

import application.dcs.IPICConvertor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


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
                String pngFilePath = baseDir + "/" + fileName;
                LOG.info("begin get jpg,jpg folder:{}", baseDir);
                int code =ipicConvertor.convertToPNG(page1, page2, 2.0f, baseDir);
                LOG.info("end get jpg,jpg folder:{},code:{}", baseDir,code);
                ipicConvertor.close();
                File file = new File(pngFilePath);
                if (file.exists()) {
                    String jpgFileName=(page1 + 1) + ".jpg";
                    String jpgFilePath=baseDir+"/"+jpgFileName;
                    handleImg(file,jpgFilePath);
                    return FilenameUtils.getBaseName(baseDir) + "/" + jpgFileName;
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

    private void handleImg(File file,String jpgFilePath) throws InterruptedException, IOException {
        Thumbnails.of(file).scale(0.5).outputFormat("jpg").toFile(new File(jpgFilePath));
    }
}
