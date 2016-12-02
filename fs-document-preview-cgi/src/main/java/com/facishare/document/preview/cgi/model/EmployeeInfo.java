package com.facishare.document.preview.cgi.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by liuq on 16/3/29.
 */
public class EmployeeInfo {
    @Getter @Setter
    private String ea;
    @Getter @Setter
    private int employeeId;
    @Getter @Setter
    private String employeeName;
    @Getter @Setter
    private int ei;
    @Getter @Setter
    private String employeeFullName;
    @Getter @Setter
    private String employeeAccount;
    @Setter
    private String sourceUser;
    public String getSourceUser() {
        return "E." + employeeId;
    }
}
