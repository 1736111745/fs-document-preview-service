package com.facishare.document.preview.common.model;

import lombok.Data;

/**
 * Created by liuq on 2017/5/13.
 */
@Data
public class ConvertOldOfficeVersionResult {
    private boolean success;
    private String errorMsg;
    private String newFilePath;
}
