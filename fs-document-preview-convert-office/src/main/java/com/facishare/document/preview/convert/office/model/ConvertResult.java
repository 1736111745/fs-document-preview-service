package com.facishare.document.preview.convert.office.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [转换结果返回类]
 * @createTime : [2022/5/10 11:01]
 * @updateUser : [Andy]
 * @updateTime : [2022/5/10 11:01]
 * @updateRemark : [重写ConvertResult类]
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvertResult {
  private boolean success;
  private String errorMsg;

  public ConvertResult(String errorMessage) {
    this.success=false;
    this.errorMsg=errorMessage;
  }
}
