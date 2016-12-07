package com.facishare.document.preview.cgi.convertor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by liuq on 16/9/9.
 */
@Slf4j
@Component
public class DocConvertor {
    public String doConvert(String path, String baseDir, String originalFilePath, int page) throws Exception {
        IDocConvertor docConvertor = getDocConvert(path);
        if (docConvertor == null) {
            return "";
        }
        try {
            String result=docConvertor.convert(page, page, originalFilePath, baseDir);
            log.info("page:{},result:{}",page,result);
            return result;
        } catch (Exception e) {
            log.error("do convert happened error:{}", e);
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
