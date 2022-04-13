package com.facishare.document.preview.convert.office.model;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author Andy
 */
@Component
@Data
public class ConvertResultInfo {
  private boolean success;
  private String errorMsg;
  private byte[] bytes;

}
