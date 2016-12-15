package com.facishare.document.preview.provider.convertor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

/**
 * Created by liuq on 16/9/9.
 */
@Slf4j
@Component
public class DocConvertor {
    public String doConvert(String path, String originalFilePath, int page) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String baseDir = FilenameUtils.getFullPathNoEndSeparator(originalFilePath);
        IDocConvertor docConvertor = getDocConvert(path);
        if (docConvertor == null) {
            return "";
        }
        String result = "";
        try {
            result = docConvertor.convert(page, page, originalFilePath, baseDir);
        } catch (Exception e) {
            log.error("do convert happened,path:{}", path, e);
        } finally {
            stopWatch.stop();
            log.info("convert file:{},page:{},result:{},cost:{}", path, page, stopWatch.getTime() + "ms", result);
            return result;
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
