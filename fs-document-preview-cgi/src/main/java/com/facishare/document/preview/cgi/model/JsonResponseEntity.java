package com.facishare.document.preview.cgi.model;

import java.io.Serializable;

/**
 * Created by liuq on 16/8/18.
 */
public class JsonResponseEntity implements Serializable {
    private boolean successed;
    private String svgData;
    private String errMsg;

    public boolean isSuccessed() {
        return successed;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }

    public String getSvgData() {
        return svgData;
    }

    public void setSvgData(String svgData) {
        this.svgData = svgData;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
