package com.facishare.document.preview.common.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * Created by liuq on 2016/11/4.
 */
@Data
@Builder
public class PreviewJsonInfo {
    @JSONField(name = "DocumentFormat")
    private String documentFormat;
    @JSONField(name = "PreviewFormat")
    private String previewFormat;
    @JSONField(name = "PageCount")
    private int pageCount;
    @JSONField(name = "ContentType")
    private String contentType;
}
