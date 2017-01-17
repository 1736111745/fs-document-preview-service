package com.facishare.document.preview.api.service;

import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.arg.GetPageCountArg;
import com.facishare.document.preview.api.model.result.ConvertDocResult;
import com.facishare.document.preview.api.model.result.GetPageCountResult;

/**
 * Created by liuq on 2016/12/14.
 */

public interface DocConvertService {
    GetPageCountResult getPageCount(GetPageCountArg arg) throws Exception;
    ConvertDocResult convertDoc(ConvertDocArg arg) throws Exception;
}