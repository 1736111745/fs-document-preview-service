package com.facishare.document.preview.cgi.model;

/**
 * Created by liuq on 16/9/6.
 */
public class DataFileInfo {
    private String dataDir;
    private String shortFilePath;
    private String originalFilePath;

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getShortFilePath() {
        return shortFilePath;
    }

    public void setShortFilePath(String shortFilePath) {
        this.shortFilePath = shortFilePath;
    }

    public String getOriginalFilePath() {
        return originalFilePath;
    }

    public void setOriginalFilePath(String originalFilePath) {
        this.originalFilePath = originalFilePath;
    }
}
