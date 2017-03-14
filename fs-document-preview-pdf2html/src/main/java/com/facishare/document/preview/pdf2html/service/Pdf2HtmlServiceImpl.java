package com.facishare.document.preview.pdf2html.service;


import com.facishare.document.preview.api.model.arg.Pdf2HtmlArg;
import com.facishare.document.preview.api.model.result.Pdf2HtmlResult;
import com.facishare.document.preview.api.service.Pdf2HtmlService;
import com.facishare.document.preview.pdf2html.utils.Pdf2HtmlHandler;
import com.fxiaoke.metrics.CounterService;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liuq on 2017/2/9.
 */
@Slf4j
public class Pdf2HtmlServiceImpl implements Pdf2HtmlService {
    @Autowired
    private CounterService counterService;
    @Autowired
    Pdf2HtmlHandler pdf2HtmlHandler;

    @Override
    public Pdf2HtmlResult convertPdf2Html(Pdf2HtmlArg arg) {
        String filePath = arg.getOriginalFilePath();
        int page = arg.getPage() + 1;
        String dataFilePath = pdf2HtmlHandler.doConvert(page, filePath);
        if (Strings.isNullOrEmpty(dataFilePath)) {
            counterService.inc("convert-pdf2html-fail");
            throw new RuntimeException("cannot convert, file: " + filePath + ",page:" + page);
        } else {
            counterService.inc("convert-pdf2html-ok");
        }
        Pdf2HtmlResult result = Pdf2HtmlResult.builder().dataFilePath(dataFilePath).build();
        return result;
    }
}
