package com.facishare.document.preview.common.dao;

/**
 * Created by liuq on 2017/3/19.
 */
public interface ConvertTaskDao {

    void addTask(String ea, String path, int page);

    int getTaskStatus(String ea,String path,int page);

    void beginExcute(String ea,String path,int page);

    void endExcute(String ea,String path,int page);
}
