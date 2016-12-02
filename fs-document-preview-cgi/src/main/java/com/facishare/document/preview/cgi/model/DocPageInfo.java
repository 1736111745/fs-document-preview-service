package com.facishare.document.preview.cgi.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by liuq on 2016/11/4.
 */
@Data
public class DocPageInfo {
    @JSONField(name = "DocumentFormat")
    private String documentFormat;
    @JSONField(name = "PreviewFormat")
    private String previewFormat;
    @JSONField(name = "PageCount")
    private int pageCount;
    @JSONField(name = "ContentType")
    private String contentType;
}
