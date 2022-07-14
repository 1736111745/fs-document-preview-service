package com.facishare.document.preview.convert.office.domain;

import java.util.List;
import lombok.Data;


@Data
public class PageInfo {
  private boolean success;
  private String errorMsg;
  private int pageCount;
  private List<String> sheetNames;

  public static PageInfo ok(int pageCount){
    PageInfo pageInfo = new PageInfo();
    pageInfo.success=true;
    pageInfo.pageCount=pageCount;
    return pageInfo;
  }

  public static PageInfo error(String errorMsg){
    PageInfo pageInfo = new PageInfo();
    pageInfo.success=false;
    pageInfo.pageCount=0;
    pageInfo.errorMsg=errorMsg;
    return pageInfo;
  }

  public static PageInfo ok(int pageCount,List<String> sheetNames){
    PageInfo pageInfo = new PageInfo();
    pageInfo.success=true;
    pageInfo.pageCount=pageCount;
    pageInfo.sheetNames=sheetNames;
    return pageInfo;
  }

}
