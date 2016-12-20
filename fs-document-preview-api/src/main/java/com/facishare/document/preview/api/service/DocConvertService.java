package com.facishare.document.preview.api.service;

import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.arg.GetPageInfoArg;
import com.facishare.document.preview.api.model.result.ConvertDocResult;
import com.facishare.document.preview.api.model.result.GetPageInfoResult;

/**
 * Created by liuq on 2016/12/14.
 */

public interface DocConvertService {
    GetPageInfoResult getPageInfo(GetPageInfoArg arg) throws Exception;

    ConvertDocResult convertDoc(ConvertDocArg arg) throws Exception;

}