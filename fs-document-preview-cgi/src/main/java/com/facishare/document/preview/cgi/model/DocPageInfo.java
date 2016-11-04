package com.facishare.document.preview.cgi.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by liuq on 2016/11/4.
 */
public class DocPageInfo {
    @JSONField(name = "DocumentFormat")
    private String documentFormat;
    @JSONField(name = "PreviewFormat")
    private String previewFormat;
    @JSONField(name = "PageCount")
    private int pageCount;
    @JSONField(name = "ContentType")
    private String contentType;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(String documentFormat) {
        this.documentFormat = documentFormat;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getPreviewFormat() {
        return previewFormat;
    }

    public void setPreviewFormat(String previewFormat) {
        this.previewFormat = previewFormat;
    }
}
