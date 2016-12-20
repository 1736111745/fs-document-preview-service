package com.facishare.document.preview.provider.service;

import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.arg.GetPageInfoArg;
import com.facishare.document.preview.api.model.result.ConvertDocResult;
import com.facishare.document.preview.api.model.result.GetPageInfoResult;
import com.facishare.document.preview.api.service.DocConvertService;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.provider.convertor.DocConvertor;
import com.facishare.document.preview.provider.utils.DocPageInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

/**
 * Created by liuq on 2016/12/14.
 */
@Slf4j
public class DocConvertServiceImpl implements DocConvertService {
    @Autowired
    DocConvertor docConvertor;
    @Autowired
    DocPageInfoHelper docPageInfoHelper;

    public GetPageInfoResult getPageInfo(GetPageInfoArg arg) throws Exception {
        String filePath = arg.getFilePath();
        byte[] bytes = FileUtils.readFileToByteArray(new File(filePath));
        PageInfo pageInfo = docPageInfoHelper.GetPageInfo(bytes, filePath);
        boolean success = pageInfo.isSuccess();
        String errorMsg = pageInfo.getErrorMsg();
        int pageCount = pageInfo.getPageCount();
        List<String> sheetNames = pageInfo.getSheetNames();
        GetPageInfoResult result = GetPageInfoResult.builder().pageCount(pageCount)
                .errorMsg(errorMsg).sheetNames(sheetNames).success(success).build();
        return result;
    }

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
