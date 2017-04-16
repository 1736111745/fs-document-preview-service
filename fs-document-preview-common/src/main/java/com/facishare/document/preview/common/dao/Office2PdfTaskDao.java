package com.facishare.document.preview.common.dao;

import java.util.List;

/**
 * Created by liuq on 2017/3/19.
 */
public interface Office2PdfTaskDao {

    int getTaskStatus(String ea,String path);

    void beginExecute(String ea, String path);

    void executeFail(String ea, String path);

    void executeSuccess(String ea, String path);

    void   addTask(String ea,String path);

}
