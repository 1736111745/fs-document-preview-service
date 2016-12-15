package com.facishare.document.preview.api.service;

import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.arg.ConvertSvg2PngArg;
import com.facishare.document.preview.api.model.arg.GetPageInfoArg;
import com.facishare.document.preview.api.model.result.ConvertDocResult;
import com.facishare.document.preview.api.model.result.ConvertSvg2PngResult;
import com.facishare.document.preview.api.model.result.GetPageInfoResult;

import java.io.IOException;

/**
 * Created by liuq on 2016/12/14.
 */
public interface DocConvertService {
    GetPageInfoResult getPageInfo(GetPageInfoArg arg) throws Exception;

    ConvertDocResult convertDoc(ConvertDocArg arg) throws Exception;

    ConvertSvg2PngResult convertSvg2Png(ConvertSvg2PngArg arg) throws IOException;
}