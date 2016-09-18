package com.facishare.document.preview.cgi.convertor;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by liuq on 16/9/9.
 */
@Component
public class DocConvertor {
    private static final Logger LOG = LoggerFactory.getLogger(DocConvertor.class);

    public String doConvert(String ea, String path, String baseDir, String name, String originalFilePath, int page,int exceptWidth) throws Exception {
        String extension = FilenameUtils.getExtension(path).toLowerCase();
        if(exceptWidth>893) exceptWidth=893;
        IDocConvertor docConvertor = null;
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
        if (docConvertor == null) {
            return "";
        }
        try {
            String filePath = docConvertor.convert(page, page, originalFilePath, baseDir,exceptWidth);
            return filePath;
        } catch (Exception e) {
            LOG.error("do convert happed error:{}", e.getStackTrace());
            return "";
        } finally {
        }
    }
}
