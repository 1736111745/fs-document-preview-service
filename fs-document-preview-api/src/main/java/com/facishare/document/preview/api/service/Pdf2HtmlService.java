package com.facishare.document.preview.api.service;

import com.facishare.document.preview.api.model.arg.Pdf2HtmlArg;
import com.facishare.document.preview.api.model.result.Pdf2HtmlResult;

/**
 * Created by liuq on 2017/2/9.
 */
public interface Pdf2HtmlService {

    Pdf2HtmlResult convertPdf2Html(Pdf2HtmlArg arg);
}
