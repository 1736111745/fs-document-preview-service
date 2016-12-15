package com.facishare.document.preview.common.model;

import lombok.Data;

import java.util.List;

@Data
public class PageInfo {
    private boolean success;
    private String errorMsg;
    private int pageCount;
    private List<String> sheetNames;

}
