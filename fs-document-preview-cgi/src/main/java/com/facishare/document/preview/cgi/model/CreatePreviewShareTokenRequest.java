package com.facishare.document.preview.cgi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author liuquan
 * @date 2021-04-08  17:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePreviewShareTokenRequest {
  public List<String> pathList;
  //安全组,网盘业务传 XiaoKeNetDisk
  public String securityGroup;
}
