package com.facishare.document.preview.cgi.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author liuquan
 * @date 2021-04-08  16:43
 */
public interface CreatePreviewShareTokens {

  class Arg implements Serializable {

    public void setEa(String ea) {
      this.ea = ea;
    }

    public void setEmployeeId(int employeeId) {
      this.employeeId = employeeId;
    }

    public String ea;
    public int employeeId;
    public List<String> pathList;
    //安全组,网盘业务传 XiaoKeNetDisk
    public String securityGroup;

    @Override
    public String toString() {
      return "Arg{" +
        "ea='" + ea + '\'' +
        ", employeeId=" + employeeId +
        ", path='" + pathList + '\'' +
        '}';
    }

  }

  class Result implements Serializable {

    public Map<String,String> fileIdMap;

    public Result() {
    }

    public Result(Map<String, String> resultMap) {
      this.fileIdMap=resultMap;
    }

    @Override
    public String toString() {
      return "Result{" +
        "fileIdMap='" + fileIdMap + '\'' +
        '}';
    }
  }
}
