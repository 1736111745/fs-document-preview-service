package com.facishare.document.preview.convert.office.model;

import lombok.Data;


/**
 * @author Andy
 */
@Data
public class ConvertResultInfo {
  private boolean success;
  private String errorMsg;
  private byte[] bytes;
}
