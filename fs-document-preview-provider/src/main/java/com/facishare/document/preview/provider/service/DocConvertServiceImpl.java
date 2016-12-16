package com.facishare.document.preview.provider.service;

import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.arg.ConvertSvg2PngArg;
import com.facishare.document.preview.api.model.arg.GetPageInfoArg;
import com.facishare.document.preview.api.model.result.ConvertDocResult;
import com.facishare.document.preview.api.model.result.ConvertSvg2PngResult;
import com.facishare.document.preview.api.model.result.GetPageInfoResult;
import com.facishare.document.preview.api.service.DocConvertService;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.provider.convertor.DocConvertor;
import com.facishare.document.preview.provider.utils.DocPageInfoHelper;
import com.facishare.document.preview.provider.utils.ImageHandler;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by liuq on 2016/12/14.
 */
public class DocConvertServiceImpl implements DocConvertService {
    @Autowired
    DocConvertor docConvertor;



    public GetPageInfoResult getPageInfo(GetPageInfoArg arg) throws Exception {
        String filePath = arg.getFilePath();
        byte[] bytes = FileUtils.readFileToByteArray(new File(filePath));
        PageInfo pageInfo = DocPageInfoHelper.GetPageInfo(bytes, filePath);
        boolean success = pageInfo.isSuccess();
        String errorMsg = pageInfo.getErrorMsg();
        int pageCount = pageInfo.getPageCount();
        List<String> sheetNames = pageInfo.getSheetNames();
        GetPageInfoResult result = GetPageInfoResult.builder().pageCount(pageCount)
                .errorMsg(errorMsg).sheetNames(sheetNames).success(success).build();
        return result;
    }

    public ConvertDocResult convertDoc(ConvertDocArg arg) throws Exception {
        String path = arg.getPath();
        String originalFilePath = arg.getOriginalFilePath();
        int page = arg.getPage();
        String dataFilePath = docConvertor.doConvert(path, originalFilePath, page);
        ConvertDocResult result = ConvertDocResult.builder().dataFilePath(dataFilePath).build();
        return result;
    }

    @Override
    public ConvertSvg2PngResult convertSvg2Png(ConvertSvg2PngArg arg) throws IOException {
        String svgFilePath = arg.getSvgFilePath();
        String pngFilePath = arg.getPngFilePath();
        boolean sucess = ImageHandler.convertSvgToPng(svgFilePath, pngFilePath);
        ConvertSvg2PngResult result = ConvertSvg2PngResult.builder().success(sucess).build();
        return result;
    }
}
