package com.facishare.document.preview.convert.office.utils;


import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;

import java.util.List;

/**
 * @author Andy
 */
public class PageInfoUtil {

  private PageInfoUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  public static PageInfo getPageInfo(ErrorInfoEnum errorMessage) {
    PageInfo pageInfo = new PageInfo();
    pageInfo.setSuccess(false);
    pageInfo.setPageCount(0);
    pageInfo.setErrorMsg(errorMessage.getErrorMsg());
    return pageInfo;
  }

  public static PageInfo getPageInfo(String errorMessage) {
    PageInfo pageInfo = new PageInfo();
    pageInfo.setSuccess(false);
    pageInfo.setPageCount(0);
    pageInfo.setErrorMsg(errorMessage);
    return pageInfo;
  }

  public static PageInfo getPageInfo(int pageCount) {
    PageInfo pageInfo = new PageInfo();
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
