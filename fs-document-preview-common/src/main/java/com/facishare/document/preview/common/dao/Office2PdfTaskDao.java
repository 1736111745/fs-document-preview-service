package com.facishare.document.preview.common.dao;

/**
 * Created by liuq on 2017/3/19.
 */
public interface Office2PdfTaskDao {

    int getTaskStatus(String ea,String path,int width);

    void beginExecute(String ea, String path,int width);

    void executeFail(String ea, String path,int width);

    void executeSuccess(String ea, String path,int width);

    void   addTask(String ea,String path,int width);

    void deleteTaskInfo(String ea,String path);

}
