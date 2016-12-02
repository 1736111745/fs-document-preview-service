package com.facishare.document.preview.cgi.model;

import lombok.Data;

import java.util.List;

/**
 * Created by liuq on 2016/10/17.
 */
@Data
public class PageInfo {
    private boolean success;
    private String errorMsg;
    private int pageCount;
    private List<String> sheetNames;

}
