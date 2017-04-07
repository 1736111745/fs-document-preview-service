package com.facishare.document.preview.common.dao.impl;

import com.facishare.document.preview.common.dao.ConvertOffice2PdfTaskDao;
import com.facishare.document.preview.common.model.Office2PdfTask;
import com.github.mongo.support.DatastoreExt;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by liuq on 2017/4/8.
 */
public class ConvertOffice2PdfTaskDaoImpl implements ConvertOffice2PdfTaskDao {

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public void addTask(String ea, String path) {
        Office2PdfTask office2PdfTask = new Office2PdfTask();
        office2PdfTask.setEa(ea);
        office2PdfTask.setPath(path);
        office2PdfTask.setCreateTime(new Date());
        office2PdfTask.setLastModifyTime(new Date());
        office2PdfTask.setStatus(0);
        dpsDataStore.insert(office2PdfTask);
        dpsDataStore.ensureIndexes();
    }

    @Override
    public int getTaskStatus(String ea, String path) {
        int status = -1;
        Query<Office2PdfTask> query = dpsDataStore.createQuery(Office2PdfTask.class);
        query.criteria("ea").equal(ea).criteria("path").equal(path);
        Office2PdfTask office2PdfTask = query.get();
        if (office2PdfTask != null) {
            status = office2PdfTask.getStatus();
        }
        return status;
    }
    private void modifyTaskStatus(String ea, String path, int status) {
        Query<Office2PdfTask> query = dpsDataStore.createQuery(Office2PdfTask.class);
        query.criteria("ea").equal(ea).criteria("path").equal(path);
        UpdateOperations<Office2PdfTask> update = dpsDataStore.createUpdateOperations(Office2PdfTask.class);
        update.set("status", status);
        update.set("lastModifyTime", new Date());
        dpsDataStore.findAndModify(query, update);
    }
    @Override
    public void beginExecute(String ea, String path) {
        modifyTaskStatus(ea, path, 1);
    }

    @Override
    public void executeFail(String ea, String path) {
        modifyTaskStatus(ea, path, 3);
    }

    @Override
    public void executeSuccess(String ea, String path) {
        modifyTaskStatus(ea, path, 2);
    }
}
