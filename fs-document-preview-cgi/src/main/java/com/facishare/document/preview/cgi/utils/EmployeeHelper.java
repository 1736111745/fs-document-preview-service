package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.cgi.model.EmployeeInfo;

/**
 * Created by liuq on 2017/5/26.
 */
public class EmployeeHelper {
  public static  EmployeeInfo createEmployeeInfo(String ea, int ei) {
    EmployeeInfo employeeInfo = new EmployeeInfo();
    employeeInfo.setEa(ea);
    employeeInfo.setEmployeeId(ei);
    return employeeInfo;
  }
}
