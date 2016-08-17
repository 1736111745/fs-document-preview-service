package com.facishare.document.preview.cgi.model;

/**
 * Created by liuq on 16/3/29.
 */
public class EmployeeInfo {
    private String ea;
    private int employeeId;
    private String employeeName;
    private int ei;
    private String employeeFullName;
    private String employeeAccount;
    private String sourceUser;
    public int getEi() {
        return ei;
    }

    public void setEi(int ei) {
        this.ei = ei;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public void setEmployeeFullName(String employeeFullName) {
        this.employeeFullName = employeeFullName;
    }

    public String getEa() {
        return ea;
    }

    public void setEa(String ea) {
        this.ea = ea;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeAccount() {
        return employeeAccount;
    }

    public void setEmployeeAccount(String employeeAccount) {
        this.employeeAccount = employeeAccount;
    }

    public String getSourceUser() {
        return "E." + employeeId;
    }

}
