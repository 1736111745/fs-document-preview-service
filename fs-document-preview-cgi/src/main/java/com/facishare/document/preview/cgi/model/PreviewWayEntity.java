package com.facishare.document.preview.cgi.model;

/**
 * Created by liuq on 16/8/25.
 */
public class PreviewWayEntity {

    private int way;
    private String previewByPathUrlFormat;
    private String previewByTokenUrlFormat;


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

    public int getWay() {
        return way;
    }

    public void setWay(int way) {
        this.way = way;
    }
}
