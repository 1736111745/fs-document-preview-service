package com.facishare.document.preview.cgi.dao;

import com.facishare.document.preview.cgi.model.DataFileInfo;

import java.io.IOException;

/**
 * Created by liuq on 16/8/16.
 */
public interface PreviewInfoDao {

    void create(String path,String baseDir, String svgFilePath, String ea, int employeeId, long docSize,int pageCount) throws IOException;

     DataFileInfo getDataFileInfo(String path, int page, String ea) throws IOException;

    String getDataFileInfo(String folderName);

    int getPageCount(String path);
}
