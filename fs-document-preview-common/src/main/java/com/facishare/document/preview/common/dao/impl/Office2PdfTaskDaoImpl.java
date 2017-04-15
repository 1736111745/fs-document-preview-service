package com.facishare.document.preview.common.dao.impl;

import com.facishare.document.preview.common.dao.Office2PdfTaskDao;
import com.facishare.document.preview.common.model.Office2PdfTask;
import com.github.mongo.support.DatastoreExt;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by liuq on 2017/3/19.
 */
@Slf4j
public class Office2PdfTaskDaoImpl implements Office2PdfTaskDao {

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public int getTaskStatus(String ea, String path, int page) {
        int status = -1;
        Query<Office2PdfTask> query = dpsDataStore.createQuery(Office2PdfTask.class);
        query.criteria("ea").equal(ea).criteria("path").equal(path).criteria("page").equal(page);
        Office2PdfTask convertTask = query.get();
        log.info(query.toString());
        if (convertTask != null) {
            status = convertTask.getStatus();
        }
        return status;
    }

    private void modifyTaskStatus(String ea, String path, int page, int status) {
        Query<Office2PdfTask> query = dpsDataStore.createQuery(Office2PdfTask.class);
        query.criteria("ea").equal(ea).criteria("path").equal(path).criteria("page").equal(page);
        UpdateOperations<Office2PdfTask> update = dpsDataStore.createUpdateOperations(Office2PdfTask.class);
        update.set("status", status);
        update.set("lastModifyTime", new Date());
        dpsDataStore.findAndModify(query, update);
    }


    public void beginExecute(String ea, String path, int page) {
        modifyTaskStatus(ea, path, page, 1);
    }

    @Override
    public void executeFail(String ea, String path, int page) {
        modifyTaskStatus(ea, path, page, 3);
    }

    @Override
    public void executeSuccess(String ea, String path, int page) {
        modifyTaskStatus(ea, path, page, 2);
    }

    @Override
    public List<Integer> batchAddTask(String ea, String path, List<Integer> pageList) {
        List<Integer> needEnqueuePageList = Lists.newArrayList();
        List<Office2PdfTask> notConvertedTasks = Lists.newArrayList();
        Query<Office2PdfTask> query = dpsDataStore.createQuery(Office2PdfTask.class);
        query.criteria("ea").equal(ea).criteria("path").equal(path).criteria("page").in(pageList);
        List<Office2PdfTask> convertedTasks = query.asList();
        List<Office2PdfTask> finalConvertedTasks = convertedTasks == null ? Lists.newArrayList() : convertedTasks;
        pageList.forEach(i -> {
            Office2PdfTask convertTask = finalConvertedTasks.stream().filter(t -> t.getPage() == i).findFirst().orElse(null);
            if (convertTask == null) {
                convertTask = new Office2PdfTask();
                convertTask.setEa(ea);
                convertTask.setPath(path);
                convertTask.setPage(i);
                convertTask.setCreateTime(new Date());
                convertTask.setLastModifyTime(new Date());
                convertTask.setStatus(0);
                notConvertedTasks.add(convertTask);
            }
        });
        if (notConvertedTasks != null && notConvertedTasks.size() > 0) {
            notConvertedTasks.forEach(t -> {
                needEnqueuePageList.add(t.getPage());
            });
            dpsDataStore.insert("Office2PdfTask", notConvertedTasks);
            dpsDataStore.ensureIndexes();
        }
        return needEnqueuePageList;
    }
}
