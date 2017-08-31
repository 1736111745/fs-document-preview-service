package com.facishare.document.preview.cgi.model;

import lombok.Data;

/**
 * Created by liuq on 2017/8/31.
 */
@Data
public class ShareTokenParamInfo {
  private String ea;
  private int employeeId;
  private String path;
  private String securityGroup;
}
