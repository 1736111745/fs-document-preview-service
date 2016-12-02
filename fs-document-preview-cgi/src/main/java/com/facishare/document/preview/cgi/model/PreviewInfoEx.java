package com.facishare.document.preview.cgi.model;

import lombok.Data;

/**
 * Created by liuq on 16/12/1.
 */
@Data
public class PreviewInfoEx {
    private boolean success;
    private String errorMsg;
    private PreviewInfo previewInfo;

}
