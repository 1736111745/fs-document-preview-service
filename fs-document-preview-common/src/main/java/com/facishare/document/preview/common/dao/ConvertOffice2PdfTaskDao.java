package com.facishare.document.preview.common.dao;

/**
 * Created by liuq on 2017/4/8.
 */
public interface ConvertOffice2PdfTaskDao {

    void addTask(String ea, String path);

    int getTaskStatus(String ea, String path);

    void beginExecute(String ea, String path);

    void executeFail(String ea, String path);

    void executeSuccess(String ea, String path);
}
