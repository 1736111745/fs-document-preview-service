package com.facishare.document.preview.convert.office.utils;

import com.facishare.document.preview.convert.office.model.PageInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Andy
 */
@Component
public class GetPageInfo {

  public PageInfo getFalsePageInfo(String errorMessage){
    PageInfo pageInfo=new PageInfo();
    pageInfo.setSuccess(false);
    pageInfo.setPageCount(0);
    pageInfo.setErrorMsg(errorMessage);
    return pageInfo;
  }

  public PageInfo getTruePageInfo(int pageCount){
    PageInfo pageInfo=new PageInfo();
    pageInfo.setSuccess(true);
    pageInfo.setPageCount(pageCount);
    return pageInfo;
  }

  public PageInfo getExcelPageInfo(int pageCount, List<String> sheetNames){
    PageInfo pageInfo=new PageInfo();
    pageInfo.setSuccess(true);
    pageInfo.setPageCount(pageCount);
    pageInfo.setSheetNames(sheetNames);
    return pageInfo;
  }
}
