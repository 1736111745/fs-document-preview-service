package com.facishare.document.preview.provider.service;

import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.result.ConvertDocResult;
import com.facishare.document.preview.api.service.DocConvertService;
import com.facishare.document.preview.provider.convertor.DocConvertor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liuq on 2016/12/14.
 */
@Slf4j
public class DocConvertServiceImpl implements DocConvertService {
    @Autowired
    DocConvertor docConvertor;

    public ConvertDocResult convertDoc(ConvertDocArg arg) throws Exception {
        log.info("begin convert doc,arg:{}", arg);
        String path = arg.getPath();
        String originalFilePath = arg.getOriginalFilePath();
        int page = arg.getPage();
        int type = arg.getType();
        String dataFilePath = docConvertor.doConvert(path, originalFilePath, page, type);
        ConvertDocResult result = ConvertDocResult.builder().dataFilePath(dataFilePath).build();
        log.info("end convert doc,result:{}", result);
        return result;
    }
}
