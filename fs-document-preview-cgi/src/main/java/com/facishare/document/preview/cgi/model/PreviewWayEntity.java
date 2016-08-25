package com.facishare.document.preview.cgi.model;

/**
 * Created by liuq on 16/8/25.
 */
public class PreviewWayEntity {

    private boolean isNewWay;
    private String previewByPathUrlFormat;
    private String previewByTokenUrlFormat;

    public boolean isNewWay() {
        return isNewWay;
    }

    public void setNewWay(boolean newWay) {
        isNewWay = newWay;
    }

    public String getPreviewByPathUrlFormat() {
        return previewByPathUrlFormat;
    }

    public void setPreviewByPathUrlFormat(String previewByPathUrlFormat) {
        this.previewByPathUrlFormat = previewByPathUrlFormat;
    }

    public String getPreviewByTokenUrlFormat() {
        return previewByTokenUrlFormat;
    }

    public void setPreviewByTokenUrlFormat(String previewByTokenUrlFormat) {
        this.previewByTokenUrlFormat = previewByTokenUrlFormat;
    }
}
