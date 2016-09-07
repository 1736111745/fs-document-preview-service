package com.facishare.document.preview.cgi.model;

import java.io.Serializable;

/**
 * Created by liuq on 16/8/18.
 */
public class JsonResponseEntity implements Serializable {
    private boolean successed;
    private String svgFile;
    private String errMsg;

    public boolean isSuccessed() {
        return successed;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }

    public String getSvgFile() {
        return svgFile;
    }

    public void setSvgFile(String svgFile) {
        this.svgFile = svgFile;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
