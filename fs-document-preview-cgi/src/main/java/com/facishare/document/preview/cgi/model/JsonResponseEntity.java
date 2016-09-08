package com.facishare.document.preview.cgi.model;

import java.io.Serializable;

/**
 * Created by liuq on 16/8/18.
 */
public class JsonResponseEntity implements Serializable {
    private boolean successed;
    private String filePath;
    private int type;
    private String errMsg;

    public boolean isSuccessed() {
        return successed;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
