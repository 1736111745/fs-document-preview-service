package com.facishare.document.preview.cgi.model;

/**
 * Created by liuq on 16/9/6.
 */
public class DataFileInfo {
    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private String baseDir;
    private String filePath;
}
