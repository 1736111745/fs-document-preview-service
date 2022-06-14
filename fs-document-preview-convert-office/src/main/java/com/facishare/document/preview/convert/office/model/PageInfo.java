package com.facishare.document.preview.convert.office.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [页码信息类]
 * @createTime : [2022/5/9 14:41]
 * @updateUser : [Andy]
 * @updateTime : [2022/5/9 14:41]
 * @updateRemark : [重写PageInfo类]
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageInfo {
  private boolean success;
  private String errorMsg;
  private int pageCount;
  private List<String> sheetNames;

  public PageInfo(int pageCount){
    success=true;
    this.pageCount=pageCount;
  }

  public PageInfo(String errorMsg){
    success=false;
    pageCount=0;
    this.errorMsg=errorMsg;
  }

  public PageInfo(int pageCount,List<String> sheetNames){
    this(pageCount);
    this.sheetNames=sheetNames;
  }
}
