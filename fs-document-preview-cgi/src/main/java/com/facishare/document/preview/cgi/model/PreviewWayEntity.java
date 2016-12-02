package com.facishare.document.preview.cgi.model;

import lombok.Data;

/**
 * Created by liuq on 16/8/25.
 */
@Data
public class PreviewWayEntity {
    private int way;
    private String previewByPathUrlFormat;
    private String previewByTokenUrlFormat;
}
