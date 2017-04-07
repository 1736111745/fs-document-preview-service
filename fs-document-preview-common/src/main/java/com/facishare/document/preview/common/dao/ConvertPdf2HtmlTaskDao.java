package com.facishare.document.preview.common.dao;

import java.util.List;

/**
 * Created by liuq on 2017/3/19.
 */
public interface ConvertPdf2HtmlTaskDao {

    int getTaskStatus(String ea,String path,int page);

    void beginExecute(String ea, String path, int page);

    void executeFail(String ea, String path, int page);

    void executeSuccess(String ea, String path, int page);

    List<Integer> batchAddTask(String ea,String path,List<Integer> pageList);

}
