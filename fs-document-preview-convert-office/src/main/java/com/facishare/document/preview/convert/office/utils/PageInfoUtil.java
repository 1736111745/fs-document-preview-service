package com.facishare.document.preview.convert.office.utils;


import com.facishare.document.preview.common.model.PageInfo;
import java.util.List;

/**
 * @author Andy
 */
public class PageInfoUtil {

  public static PageInfo getFalsePageInfo(String errorMessage){
    PageInfo pageInfo=new PageInfo();
    pageInfo.setSuccess(false);
    pageInfo.setPageCount(0);
    pageInfo.setErrorMsg(errorMessage);
    return pageInfo;
  }

  public static PageInfo getTruePageInfo(int pageCount){
    PageInfo pageInfo=new PageInfo();
    pageInfo.setSuccess(true);
    pageInfo.setPageCount(pageCount);
    return pageInfo;
  }

  public static PageInfo getExcelPageInfo(int pageCount, List<String> sheetNames){
    PageInfo pageInfo=new PageInfo();
    pageInfo.setSuccess(true);
    pageInfo.setPageCount(pageCount);
    pageInfo.setSheetNames(sheetNames);
    return pageInfo;
  }
}
