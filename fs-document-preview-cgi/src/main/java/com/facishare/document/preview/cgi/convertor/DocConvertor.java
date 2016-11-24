package com.facishare.document.preview.cgi.convertor;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by liuq on 16/9/9.
 */
@Component
public class DocConvertor {
    private static final Logger logger = LoggerFactory.getLogger(DocConvertor.class);
    public String doConvert(String path, String baseDir, String originalFilePath, int page) throws Exception {
        IDocConvertor docConvertor = getDocConvert(path);
        if (docConvertor == null) {
            return "";
        }
        try {
            StopWatch watch=new StopWatch();
            watch.start();
            logger.info("begin do convert,baseDir:{},originalFilePath:{}", baseDir, originalFilePath);
            String filePath = docConvertor.convert(page, page, originalFilePath, baseDir);
            watch.stop();
            logger.info("end do convert,filePath:{},cost:{}", filePath,watch.getTime()+"ms");
            return filePath;
        } catch (Exception e) {
            logger.error("do convert happened error:{}", e);
            return "";
        }
    }
    public String doConvert(String path, String baseDir, String originalFilePath, int page, int width) throws Exception {
        IDocConvertor docConvertor = getDocConvert(path);
        if (docConvertor == null) {
            return "";
        }
        try {
            StopWatch watch=new StopWatch();
            watch.start();
            logger.info("begin do convert,baseDir:{},originalFilePath:{}", baseDir, originalFilePath);
            String filePath = docConvertor.convert(page, page, originalFilePath, baseDir, width);
            watch.stop();
            logger.info("end do convert,filePath:{},cost:{}", filePath,watch.getTime()+"ms");
            return filePath;
        } catch (Exception e) {
            logger.error("do convert happened error:{}", e);
            return "";
        }
    }

    private IDocConvertor getDocConvert(String path) {
        IDocConvertor docConvertor = null;
        String extension = FilenameUtils.getExtension(path).toLowerCase();
        switch (extension) {
            case "doc":
            case "docx":
                docConvertor = new WordConvertor();
                break;
            case "xls":
            case "xlsx":
                docConvertor = new ExcelConvertor();
                break;
            case "ppt":
            case "pptx":
                docConvertor = new PPTConvertor();
                break;
            case "pdf":
                docConvertor = new PDFConvertor();
                break;
        }
        return docConvertor;
    }
}
