package com.facishare.document.preview.convert.office.domain;

import lombok.Data;

@Data
public class ConvertResult {
  private boolean success;
  private String errorMsg;
  public static ConvertResult error(String errorMessage) {
    ConvertResult result = new ConvertResult();
    result.success = false;
    result.errorMsg =errorMessage;
    return result;
  }

}
