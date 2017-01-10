package com.facishare.document.preview.api.service;

import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.result.ConvertDocResult;

/**
 * Created by liuq on 2016/12/14.
 */

public interface DocConvertService {

    ConvertDocResult convertDoc(ConvertDocArg arg) throws Exception;
}