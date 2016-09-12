package com.facishare.document.preview.cgi.utils;

import com.github.autoconf.ConfigFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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
            dataDir = config.get("dataDir");
            tempDir = config.get("tempDir");
        });
        this.ea = ea;
    }

    public PathHelper() {
    }

    public String getConvertTempPath() {
        String convertorTempPath = String.format("%s/convertor/", tempDir);
        return convertorTempPath;
    }


    public String getTempFilePath(String path, byte[] bytes) throws IOException {
        String extension = FilenameUtils.getExtension(path).toLowerCase();
        String tempFileName = SampleUUID.getUUID() + "." + extension;
        String tempFilePath = String.format("%s/tempfile/%s", tempDir, tempFileName);
        FileUtils.writeByteArrayToFile(new File(tempFilePath), bytes);
        return tempFilePath;
    }

    public String getDataFolder() throws IOException {
        String yyyyMM = DateUtil.getFormatDateStr("yyyyMM");
        String dd = DateUtil.getFormatDateStr("dd");
        String hh = DateUtil.getFormatDateStr("HH");
        String dirPath = String.format("%s/%s/%s/%s/%s/%s/%s", dataDir, "dps", yyyyMM, dd, hh, ea, SampleUUID.getUUID());
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }
}
