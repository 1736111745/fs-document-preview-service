package com.facishare.document.preview.provider.service;

import com.facishare.document.preview.api.model.arg.AsyncConvertDocArg;
import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.arg.GetPageCountArg;
import com.facishare.document.preview.api.model.result.AsyncConvertDocResult;
import com.facishare.document.preview.api.model.result.ConvertDocResult;
import com.facishare.document.preview.api.model.result.GetPageCountResult;
import com.facishare.document.preview.api.service.DocConvertService;
import com.facishare.document.preview.provider.convertor.ConvertorHelper;
import com.facishare.document.preview.provider.convertor.DocConvertor;
import com.fxiaoke.metrics.CounterService;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liuq on 2016/12/14.
 */
@Slf4j
public class DocConvertServiceImpl implements DocConvertService {
  @Autowired
  private DocConvertor docConvertor;
  @Autowired
  private CounterService counterService;

  @Override
  public GetPageCountResult getPageCount(GetPageCountArg arg) throws Exception {
    log.info("begin get page count:{}", arg);
    int pageCount = ConvertorHelper.getOldWordOrPPTPageCount(arg.getFilePath());
    if (pageCount < 0) {
      counterService.inc("get-page-count-fail");
      throw new RuntimeException("convertor is busy, file: " + arg.getFilePath());
    } else {
      counterService.inc("get-page-count-ok");
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
      counterService.inc("convert-fail");
      throw new RuntimeException("cannot convert, file: " + originalFilePath);
    } else {
      counterService.inc("convert-ok");
    }
    ConvertDocResult result = ConvertDocResult.builder().dataFilePath(dataFilePath).build();
    log.info("end convert doc,result:{}", result);
    return result;
  }

  @Override
  public AsyncConvertDocResult asyncConvertDoc(AsyncConvertDocArg arg) throws Exception {
    return null;
  }
}
