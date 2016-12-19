package com.facishare.document.preview.cgi.dao.impl;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.common.utils.DateUtil;
import com.github.mongo.support.DatastoreExt;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档预览dao
 * Created by liuq on 16/8/16.
 */
@Slf4j
@Repository
public class PreviewInfoDaoImpl implements PreviewInfoDao {

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public void savePreviewInfo(String ea, String path, String dataFilePath) {
        String dataFileName = FilenameUtils.getName(dataFilePath);
        synchronized (this) {
            Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
            query.criteria("path").equal(path).criteria("ea").equal(ea);
            PreviewInfo previewInfo = query.get();
            List<String> filePathList = previewInfo.getFilePathList();
            if (filePathList == null)
                filePathList = Lists.newArrayList();
            filePathList.add(dataFileName);
            filePathList = filePathList.stream().sorted((o1, o2) -> NumberUtils.toInt(FilenameUtils.getBaseName(o1)) - NumberUtils.toInt(FilenameUtils.getBaseName(o2))).collect(Collectors.toList());
            UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
            update.set("filePathList", filePathList);
            dpsDataStore.findAndModify(query, update);
        }
    }

    @Override
    public String getDataFilePath(String path, int page, String dataDir, List<String> filePathList) throws IOException {
        String dataFilePath = "";
        String fileExtension = FilenameUtils.getExtension(path).toLowerCase();
        fileExtension = fileExtension.substring(0, fileExtension.length() - 1);
        String dataFileName = "";
        if (filePathList != null && filePathList.size() > 0) {
            switch (fileExtension) {
                case "pdf": {
                    int pageIndex = page + 1;
                    dataFileName = filePathList.stream().filter(x -> (x.equals(pageIndex + ".jpg") || x.equals(pageIndex + ".png"))).findFirst().orElse("");
                    break;
                }
                case "xls": {
                    int pageIndex = page + 1;
                    dataFileName = filePathList.stream().filter(x -> x.equals(pageIndex + ".html")).findFirst().orElse("");
                    break;
                }
                case "doc": {
                    int pageIndex = page + 1;
                    dataFileName = filePathList.stream().filter(x -> x.equals(pageIndex + ".jpg") || (x.equals(pageIndex + ".png") || x.equals(pageIndex + ".svg"))).findFirst().orElse("");
                    break;
                }
                case "ppt": {
                    int pageIndex = page;
                    dataFileName = filePathList.stream().filter(x -> x.equals(pageIndex + ".jpg") || (x.equals(pageIndex + ".png") || x.equals(pageIndex + ".svg"))).findFirst().orElse("");
                    break;
                }
            }
        }
        if (!Strings.isNullOrEmpty(dataFileName)) {
            dataFilePath = FilenameUtils.concat(dataDir, dataFileName);
        }
        return dataFilePath;
    }

    @Override
    public String getBaseDir(String dirName) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("dirName").equal(dirName);
        PreviewInfo previewInfo = query.get();
        return previewInfo.getDataDir();
    }


    @Override
    public PreviewInfo initPreviewInfo(String ea, int employeeId, String path, String originalFilePath, String dataDir, long docSize, int pageCount, List<String> sheetNames) {
        PreviewInfo previewInfo = new PreviewInfo();
        synchronized (this) {
            previewInfo.setDocSize(docSize);
            previewInfo.setDirName(FilenameUtils.getBaseName(dataDir));
            previewInfo.setCreateTime(new Date());
            int yyyyMMdd = DateUtil.getFormatDateInt("yyyyMMdd");
            previewInfo.setCreateYYMMDD(yyyyMMdd);
            previewInfo.setEa(ea);
            previewInfo.setEmployeeId(employeeId);
            previewInfo.setDataDir(dataDir);
            previewInfo.setPath(path);
            previewInfo.setSheetNames(sheetNames);
            previewInfo.setPageCount(pageCount);
            previewInfo.setOriginalFilePath(originalFilePath);
            List<String> filePathList = new ArrayList<>();
            previewInfo.setFilePathList(filePathList);
            dpsDataStore.insert("PreviewInfo", previewInfo);
            dpsDataStore.ensureIndexes();
        }
        return previewInfo;
    }

    @Override
    public PreviewInfo getInfoByPath(String ea, String path) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        return query.get();
    }
}
