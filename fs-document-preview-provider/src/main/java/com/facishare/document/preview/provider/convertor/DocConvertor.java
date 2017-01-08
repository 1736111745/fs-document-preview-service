package com.facishare.document.preview.provider.convertor;

import com.facishare.document.preview.common.utils.DocType;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by liuq on 16/9/9.
 */
@Slf4j
@Component
public class DocConvertor {
    public String doConvert(String path, String originalFilePath, int page, int type) throws Exception {
        DocType docType = DocTypeHelper.getDocType(path);
        String resultFilePath = "";
        switch (docType) {
            case Word: {
                IDocConvertor docConvertor = new WordConvertor();
                resultFilePath = type == 1 ? docConvertor.convert2Svg(originalFilePath, page, page) : docConvertor.convert2Png(originalFilePath, page, page);
                break;
            }
            case PPT: {
                IDocConvertor docConvertor = new PPTConvertor();
                resultFilePath = type == 1 ? docConvertor.convert2Svg(originalFilePath, page, page) : docConvertor.convert2Png(originalFilePath, page, page);
                break;
            }
            case Excel: {
                IDocConvertor docConvertor = new ExcelConvertor();
                resultFilePath = docConvertor.convert2Html(originalFilePath, page);
                break;
            }
            case PDF: {
                IDocConvertor docConvertor = new PDFConvertor();
                resultFilePath = docConvertor.convert2Png(originalFilePath, page, page);
            }
        }
        return resultFilePath;
    }

    public static void main(String[] args) throws Exception {
        String path="/Users/liuq/Downloads/【纷享公开课】高效时间管理.ppt";
        String filePath="/Users/liuq/Downloads/【纷享公开课】高效时间管理.ppt";

        new DocConvertor().doConvert(path,filePath,0,2);
    }
}
