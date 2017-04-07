package com.facishare.document.preview.common.dao.impl;

import com.facishare.document.preview.common.dao.ConvertPdf2HtmlTaskDao;
import com.facishare.document.preview.common.model.Pdf2HtmlTask;
import com.github.mongo.support.DatastoreExt;
import com.google.common.collect.Lists;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by liuq on 2017/3/19.
 */
public class ConvertPdf2HtmlTaskDaoImpl implements ConvertPdf2HtmlTaskDao {

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public int getTaskStatus(String ea, String path, int page) {
        int status = -1;
        Query<Pdf2HtmlTask> query = dpsDataStore.createQuery(Pdf2HtmlTask.class);
        query.criteria("ea").equal(ea).criteria("path").equal(path).criteria("page").equal(page);
        Pdf2HtmlTask convertTask = query.get();
        if (convertTask != null) {
            status = convertTask.getStatus();
        }
        return status;
    }

    private void modifyTaskStatus(String ea, String path, int page, int status) {
        Query<Pdf2HtmlTask> query = dpsDataStore.createQuery(Pdf2HtmlTask.class);
        query.criteria("ea").equal(ea).criteria("path").equal(path).criteria("page").equal(page);
        UpdateOperations<Pdf2HtmlTask> update = dpsDataStore.createUpdateOperations(Pdf2HtmlTask.class);
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
        List<Pdf2HtmlTask> notConvertedTasks = Lists.newArrayList();
        Query<Pdf2HtmlTask> query = dpsDataStore.createQuery(Pdf2HtmlTask.class);
        query.criteria("ea").equal(ea).criteria("path").equal(path).criteria("page").in(pageList);
        List<Pdf2HtmlTask> convertedTasks = query.asList();
        List<Pdf2HtmlTask> finalConvertedTasks = convertedTasks == null ? Lists.newArrayList() : convertedTasks;
        pageList.forEach(i -> {
            Pdf2HtmlTask convertTask = finalConvertedTasks.stream().filter(t -> t.getPage() == i).findFirst().orElse(null);
            if (convertTask == null) {
                convertTask = new Pdf2HtmlTask();
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
            dpsDataStore.insert("Pdf2HtmlTask", notConvertedTasks);
            dpsDataStore.ensureIndexes();
        }
        return needEnqueuePageList;
    }
}
