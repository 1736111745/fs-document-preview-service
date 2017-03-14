package com.facishare.document.preview.cgi.model;

import com.facishare.document.preview.common.model.PreviewInfo;
import lombok.Data;
import lombok.ToString;

/**
 * Created by liuq on 16/12/1.
 */
@Data
@ToString
public class PreviewInfoEx {
    private boolean success;
    private String errorMsg;
    private PreviewInfo previewInfo;

}
