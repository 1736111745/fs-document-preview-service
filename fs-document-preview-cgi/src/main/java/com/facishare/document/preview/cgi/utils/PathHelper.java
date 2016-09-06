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


    public String getDataDir()
    {
        return this.dataDir;
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

    public String getSvgFolder(String path) throws IOException {
        String yyyyMM = DateUtil.getFormatDateStr("yyyyMM");
        String dd = DateUtil.getFormatDateStr("dd");
        String hh = DateUtil.getFormatDateStr("HH");
        //创建根目录
        String extension = FilenameUtils.getExtension(path).toLowerCase();
        String type;
        switch (extension) {
            case "doc":
            case "docx":
                type = "word";
                break;
            case "xls":
            case "xlsx":
                type = "excel";
                break;
            case "ppt":
            case "pptx":
                type = "ppt";
                break;
            case "pdf":
                type = "pdf";
                break;
            default:
                type = "other";
                break;
        }
        String dirPath = String.format("%s/%s/%s/%s/%s/%s/%s/%s", dataDir, "dps", yyyyMM, dd, hh, ea, type,SampleUUID.getUUID());
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }
}
