package com.facishare.document.preview.office2pdf.model;

import lombok.Data;

/**
 * @author Andy
 */
@Data
public class ConverResultInfo {
    private boolean success;
    private String errorMsg;
    private byte[] bytes;
}
