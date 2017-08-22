package com.facishare.document.preview.common.dao.impl;

import com.facishare.document.preview.common.dao.Office2PdfTaskDao;
import com.facishare.document.preview.common.model.Office2PdfTask;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.github.mongo.support.DatastoreExt;
import lombok.extern.slf4j.Slf4j;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by liuq on 2017/3/19.
 */
@Slf4j
public class Office2PdfTaskDaoImpl implements Office2PdfTaskDao {

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public int getTaskStatus(String ea, String path,int width) {
        int status = -1;
        Query<Office2PdfTask> query = dpsDataStore.createQuery(Office2PdfTask.class);
        createQuery(query,ea,path,width);
        Office2PdfTask convertTask = query.get();
        if (convertTask != null) {
            status = convertTask.getStatus();
        }
        return status;
    }

    private void createQuery(Query<Office2PdfTask> query, String ea, String path, int width)
    {
        if (width == 1000) {
            query.and(query.criteria("ea").equal(ea).criteria("path").equal(path))
                 .and(query.or(query.criteria("width").equal(width)).criteria("width").doesNotExist());
        } else {
            query.and(query.criteria("path").equal(path).criteria("ea").equal(ea)).and(query.or(query.criteria("width").equal(width)));
        }
    }

    private void modifyTaskStatus(String ea, String path, int width,int status) {
        Query<Office2PdfTask> query = dpsDataStore.createQuery(Office2PdfTask.class);
        createQuery(query,ea,path,width);
        UpdateOperations<Office2PdfTask> update = dpsDataStore.createUpdateOperations(Office2PdfTask.class);
        update.set("status", status);
        update.set("lastModifyTime", new Date());
        dpsDataStore.findAndModify(query, update);
    }


    public void beginExecute(String ea, String path,int width) {
        modifyTaskStatus(ea, path, 1,width);
    }

    @Override
    public void executeFail(String ea, String path,int width) {
        modifyTaskStatus(ea, path, 3,width);
    }

    @Override
    public void executeSuccess(String ea, String path,int width) {
        modifyTaskStatus(ea, path, 2,width);
    }

    @Override
    public void  addTask(String ea, String path,int width) {
        Office2PdfTask convertTask = new Office2PdfTask();
        convertTask.setEa(ea);
        convertTask.setPath(path);
        convertTask.setCreateTime(new Date());
        convertTask.setWidth(width);
        convertTask.setLastModifyTime(new Date());
        convertTask.setStatus(0);
        dpsDataStore.insert(convertTask);
        dpsDataStore.ensureIndexes();
    }


}
