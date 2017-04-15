package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.common.utils.Office2PdfApiHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by liuq on 2017/4/13.
 */
@Component
@Slf4j
public class Office2PdfHandler {
    @Autowired
    Office2PdfApiHelper office2PdfApiHelper;

    public String ConvertOffice2Pdf(String filePath, int page) throws Exception {
        int pageIndex = page - 1;
        byte[] pdfFileBytes = office2PdfApiHelper.getPdfBytes(filePath, pageIndex);
        if (pdfFileBytes == null) return null;
        String pdfPageFilePath = filePath + "." + page + ".pdf";
        log.info("pdfPageFilePath:{}", pdfPageFilePath);
        FileUtils.writeByteArrayToFile(new File(pdfPageFilePath), pdfFileBytes);
        return pdfPageFilePath;
    }
}
