package com.facishare.document.preview.cgi.convertor;

import application.dcs.IPICConvertor;
import com.facishare.document.preview.cgi.utils.ImageHandle;
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
        ImageHandle.fromFile(file).scale(0.5).toFile(new File(jpgFilePath));
    }

//    public static void main(String[] args) {
//        File file=new File("/Users/liuq/smb_data/normal/dps/201609/22/17/fssdetest/8h4jpozk/2.png");
//        File file1=new File("/Users/liuq/smb_data/normal/dps/201609/22/17/fssdetest/8h4jpozk/a.png");
//        ImageHandle.fromFile(file).scale(0.5).toFile(file);
//    }
}
