package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.github.autoconf.ConfigFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuq on 16/8/16.
 */
public class PathHelper {

    private String tempDir;
    private String dataDir;

    private EmployeeInfo employeeInfo;

    public PathHelper(EmployeeInfo employeeInfo) {
        ConfigFactory.getConfig("fs-dps-config", config -> {
            dataDir = config.get("dataDir");
            tempDir = config.get("tempDir");
        });
        this.employeeInfo = employeeInfo;
    }

    public PathHelper() {
    }


    public String getDataDir()
    {
        return this.dataDir;
    }

    public String getConvertTempPath() {
        ConfigFactory.getConfig("fs-dps-config", config -> {
            dataDir = config.get("dataDir");
            tempDir = config.get("tempDir");
        });
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

    public String getHtmlFilePath(String path) throws IOException {
        String yyyyMM = getFormatDateStr("yyyyMM");
        String dd = getFormatDateStr("dd");
        String hh = getFormatDateStr("HH");
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
        String dirPath = String.format("%s/%s/%s/%s/%s/%s/%s", dataDir, "dps", yyyyMM, dd, hh, employeeInfo.getEa(), type);
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = SampleUUID.getUUID() + ".html";
        String filePath = dirPath + "/" + fileName;
        return filePath;
    }

    public static String getFormatDateStr(String f) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(f);
        String yyMM = format.format(date);
        return yyMM;
    }
}
