package com.facishare.document.preview.common.dao.impl;

import com.facishare.document.preview.common.dao.ConvertTaskDao;
import com.facishare.document.preview.common.model.ConvertTask;
import com.github.mongo.support.DatastoreExt;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by liuq on 2017/3/19.
 */
public class ConvertTaskDaoImpl implements ConvertTaskDao {

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public void addTask(String ea, String path, int page) {
        ConvertTask convertTask = new ConvertTask();
        convertTask.setEa(ea);
        convertTask.setPath(path);
        convertTask.setPage(page);
        convertTask.setCreateTime(new Date());
        convertTask.setLastModifyTime(new Date());
        convertTask.setStatus(0);
        dpsDataStore.insert("ConvertTask", convertTask);
        dpsDataStore.ensureIndexes();
    }

    @Override
    public int getTaskStatus(String ea, String path, int page) {
        int status = -1;
        Query<ConvertTask> query = dpsDataStore.createQuery(ConvertTask.class);
        query.criteria("ea").equal(ea).criteria("path").equal(path).criteria("page").equal(page);
        ConvertTask convertTask = query.get();
        if (convertTask != null) {
            status = convertTask.getStatus();
        }
        return status;
    }

    private void modifyTaskStatus(String ea, String path, int page,int status) {
        Query<ConvertTask> query = dpsDataStore.createQuery(ConvertTask.class);
        query.criteria("ea").equal(ea).criteria("path").equal(path).criteria("page").equal(page);
        UpdateOperations<ConvertTask> update = dpsDataStore.createUpdateOperations(ConvertTask.class);
        update.set("status", status);
        update.set("lastModifyTime", new Date());
        dpsDataStore.findAndModify(query, update);
    }


    public void  beginExcute(String ea,String path,int page)
    {
        modifyTaskStatus(ea,path,page,1);
    }

    @Override
    public void excuteFail(String ea, String path, int page) {
        modifyTaskStatus(ea, path, page, 3);
    }

    @Override
    public void excuteSuccess(String ea, String path, int page) {
        modifyTaskStatus(ea, path, page, 2);
    }

}
