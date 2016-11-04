package com.facishare.document.preview.cgi.convertor;

import application.dcs.IPICConvertor;
import com.fxiaoke.common.PasswordUtil;
import com.google.common.net.UrlEscapers;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
                LOG.info("begin get image,folder:{},page:{}", baseDir, page1);
                int code = ipicConvertor.convertToPNG(page1, page2, 2f, baseDir);
                LOG.info("end get image,folder:{},code:{},page:{}", baseDir, code, page1);
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

    public static void main(String[] args) throws IOException {
        String mongoServer = "mongo.servers=mongodb://warehouse_user02:B07C5EB74C9D95D07BAFBB0059490090EA8F11A4E09AE97C2369D7CD442A52EC@172.17.44.7:27017,172.17.45.7:27017,172.17.46.7:27017";
        Pattern p = Pattern.compile("mongodb://((.+):(.*)@)");
        Matcher m = p.matcher(mongoServer);
        if (m.find()) {
            try {
                String pwd = UrlEscapers.urlFormParameterEscaper().escape(PasswordUtil.decode(m.group(3)));
                mongoServer = mongoServer.substring(0, m.end(2) + 1) + pwd + mongoServer.substring(m.end(1) - 1);
            } catch (Exception e) {
            }
        }
    }
}
