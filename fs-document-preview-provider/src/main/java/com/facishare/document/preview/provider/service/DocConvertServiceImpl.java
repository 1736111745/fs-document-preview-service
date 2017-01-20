package com.facishare.document.preview.provider.service;

import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.arg.GetPageCountArg;
import com.facishare.document.preview.api.model.result.ConvertDocResult;
import com.facishare.document.preview.api.model.result.GetPageCountResult;
import com.facishare.document.preview.api.service.DocConvertService;
import com.facishare.document.preview.provider.convertor.ConvertorHelper;
import com.facishare.document.preview.provider.convertor.DocConvertor;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liuq on 2016/12/14.
 */
@Slf4j
public class DocConvertServiceImpl implements DocConvertService {
    @Autowired
    DocConvertor docConvertor;

    @Override
    public GetPageCountResult getPageCount(GetPageCountArg arg) throws Exception {
        log.info("begin get page count:{}",arg);
        int pageCount= ConvertorHelper.getOldWordOrPPTPageCount(arg.getFilePath());
        if (pageCount < 0) {
            throw new RuntimeException("convertor is busy, file: " + arg.getFilePath());
        }
        return GetPageCountResult.builder().pageCount(pageCount).build();
    }

    public ConvertDocResult convertDoc(ConvertDocArg arg) throws Exception {
        log.info("begin convert doc,arg:{}", arg);
        String path = arg.getPath();
        String originalFilePath = arg.getOriginalFilePath();
        int page = arg.getPage();
        int type = arg.getType();
        String dataFilePath = docConvertor.doConvert(path, originalFilePath, page, type);
        if (Strings.isNullOrEmpty(dataFilePath)) {
            throw new RuntimeException("cannot convert, file: " + originalFilePath);
        }
        ConvertDocResult result = ConvertDocResult.builder().dataFilePath(dataFilePath).build();
        log.info("end convert doc,result:{}", result);
        return result;
    }
}
