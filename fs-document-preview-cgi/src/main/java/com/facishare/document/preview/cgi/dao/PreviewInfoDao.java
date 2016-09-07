package com.facishare.document.preview.cgi.dao;

import com.facishare.document.preview.cgi.model.SvgFileInfo;

import java.io.IOException;

/**
 * Created by liuq on 16/8/16.
 */
public interface PreviewInfoDao {

    void create(String path,String baseDir, String svgFilePath, String ea, int employeeId, long docSize) throws IOException;

    SvgFileInfo getSvgBaseDir(String path, int page, String ea) throws IOException;

    String getSvgBaseDir(String folderName);

    int getPageCount(String path);
}
