package com.facishare.document.preview.cgi.model;

import java.io.Serializable;

/**
 * Created by liuq on 16/8/18.
 */
public class JsonResponseEntity implements Serializable {
    private boolean successed;
    private String errorMsg;

    public boolean isSuccessed() {
        return successed;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
