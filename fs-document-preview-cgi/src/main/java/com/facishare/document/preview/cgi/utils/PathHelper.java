package com.facishare.document.preview.cgi.utils;

import com.github.autoconf.ConfigFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuq on 16/8/16.
 */
public class PathHelper {

    private String tempDir;
    private String dataDir;
    private String ea;

    public PathHelper(String ea) {
        ConfigFactory.getConfig("fs-dps-config", config -> {
            this.dataDir = config.get("dataDir");
            this.tempDir = config.get("tempDir");
        });
        this.ea = ea;
    }

    public PathHelper() {
        ConfigFactory.getConfig("fs-dps-config", config -> {
            this.dataDir = config.get("dataDir");
            this.tempDir = config.get("tempDir");
        });
    }

    public String getConvertTempPath() {
        //debug
        this.tempDir="/Users/liuq/temp/docconvert/temp";
        String convertorTempPath = String.format("%s/convertor/", this.tempDir);
        return convertorTempPath;
    }


    public String getDataDir() throws IOException {
        this.dataDir="/Users/liuq/temp/docconvert/normal";
        String yyyyMM = DateUtil.getFormatDateStr("yyyyMM");
        String dd = DateUtil.getFormatDateStr("dd");
        String hh = DateUtil.getFormatDateStr("HH");
        String dirPath = String.format("%s/%s/%s/%s/%s/%s/%s", this.dataDir, "dps", yyyyMM, dd, hh, ea, SampleUUID.getUUID());
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }
}
