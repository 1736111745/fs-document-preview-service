package com.facishare.document.preview.cgi.dao;

import com.facishare.document.preview.cgi.model.PreviewInfo;

/**
 * Created by liuq on 16/8/16.
 */
public interface PreviewInfoDao {

    void create(String path,String filePath,String ea,int employeeId,long docSize);

    PreviewInfo getInfo(String condition,int type);
}
