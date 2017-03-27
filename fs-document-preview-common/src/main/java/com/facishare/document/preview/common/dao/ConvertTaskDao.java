package com.facishare.document.preview.common.dao;

import java.util.List;

/**
 * Created by liuq on 2017/3/19.
 */
public interface ConvertTaskDao {

    void addTask(String ea, String path, int page);

    int getTaskStatus(String ea,String path,int page);

    void beginExcute(String ea,String path,int page);

    void excuteFail(String ea,String path,int page);

    void excuteSuccess(String ea,String path,int page);

    List<Integer> batchAddTask(String ea,String path,List<Integer> pageList);

}
