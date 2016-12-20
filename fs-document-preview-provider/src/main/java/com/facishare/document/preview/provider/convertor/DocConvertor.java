package com.facishare.document.preview.provider.convertor;

import com.facishare.document.preview.common.utils.DocType;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liuq on 16/9/9.
 */
@Slf4j
@Component
public class DocConvertor {
    @Autowired
    WordConvertor wordConvertor;
    @Autowired
    ExcelConvertor excelConvertor;
    @Autowired
    PPTConvertor pptConvertor;
    @Autowired
    PDFConvertor pdfConvertor;


    public String doConvert(String path, String originalFilePath, int page, int type) throws Exception {
        DocType docType = DocTypeHelper.getDocType(path);
        String resultFilePath = "";
        switch (docType) {
            case Word: {
                resultFilePath = type == 1 ? wordConvertor.convert2Svg(originalFilePath, page, page) : wordConvertor.convert2Png(originalFilePath, page, page);
                break;
            }
            case PPT: {
                resultFilePath = type == 1 ? wordConvertor.convert2Svg(originalFilePath, page, page) : wordConvertor.convert2Png(originalFilePath, page, page);
                break;
            }
            case Excel: {
                resultFilePath = excelConvertor.convert2Html(originalFilePath, page, page);
                break;
            }
            case PDF: {
                resultFilePath = pdfConvertor.convert2Png(originalFilePath, page, page);
            }
        }
        return resultFilePath;
    }
}
