package com.facishare.document.preview.cgi.dao;

import com.facishare.document.preview.cgi.model.DataFileInfo;
import com.facishare.document.preview.cgi.model.PreviewInfo;

import java.io.IOException;
import java.util.List;

/**
 * Created by liuq on 16/8/16.
 */
public interface PreviewInfoDao {

    void create(String path, String baseDir, String dataFilePath, String ea, int employeeId, long docSize, int pageCount) throws IOException;

    DataFileInfo getDataFileInfo(String path, int page, String ea) throws IOException;

    String getBaseDir(String folderName);

    void initPreviewInfo(String path, String originalFilePath, String dataDir, long docSize, int pageCount, List<String> sheetNames, String ea, int employeeId);

    PreviewInfo getInfoByPath(String path);

}
