package com.facishare.document.preview.convert.office.model;

import lombok.Data;

import java.util.List;

@Data
public class PageInfo {
    private boolean success;
    private String errorMsg;
    private int pageCount;
    private List<String> sheetNames;

}
