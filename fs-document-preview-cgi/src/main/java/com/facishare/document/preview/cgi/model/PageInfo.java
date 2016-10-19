package com.facishare.document.preview.cgi.model;

import java.util.List;

/**
 * Created by liuq on 2016/10/17.
 */
public class PageInfo {
    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<String> getSheetNames() {
        return sheetNames;
    }

    public void setSheetNames(List<String> sheetNames) {
        this.sheetNames = sheetNames;
    }

    private int pageCount;
    private List<String> sheetNames;
}
