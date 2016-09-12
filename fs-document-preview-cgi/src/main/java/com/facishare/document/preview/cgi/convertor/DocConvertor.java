package com.facishare.document.preview.cgi.convertor;

import com.facishare.document.preview.cgi.utils.PathHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by liuq on 16/9/9.
 */
@Component
public class DocConvertor {
    private static final Logger LOG = LoggerFactory.getLogger(DocConvertor.class);

    public String doConvert(String ea, String path, String baseDir, String name, byte[] bytes, int page) throws Exception {
        PathHelper pathHelper = new PathHelper(ea);
        String tempFilePath = pathHelper.getTempFilePath(path, bytes);
        String extension = FilenameUtils.getExtension(path).toLowerCase();
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
            String filePath = docConvertor.convert(page, page, tempFilePath, baseDir);
            return filePath;
        } catch (Exception e) {
            LOG.info("do convert happed error:{}", e.getStackTrace());
            return "";
        } finally {
            FileUtils.deleteQuietly(new File(tempFilePath));
        }
    }
}
