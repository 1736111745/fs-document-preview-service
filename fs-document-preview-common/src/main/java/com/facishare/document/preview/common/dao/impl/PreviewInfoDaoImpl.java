package com.facishare.document.preview.common.dao.impl;

import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.DocType;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.DateUtil;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import com.github.mongo.support.DatastoreExt;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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
    @Synchronized
    public void savePreviewInfo(String ea, String path, String dataFilePath) {
        String dataFileName = FilenameUtils.getName(dataFilePath);
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        PreviewInfo previewInfo = query.get();
        List<String> filePathList = previewInfo.getFilePathList();
        if (filePathList == null)
            filePathList = Lists.newArrayList();
        filePathList.add(dataFileName);
        filePathList = filePathList.stream().sorted(Comparator.comparingInt(o -> NumberUtils.toInt(FilenameUtils.getBaseName(o)))).collect(Collectors.toList());
        UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
        update.set("filePathList", filePathList);
        dpsDataStore.findAndModify(query, update);
    }

    @Override
    public String getDataFilePath(String path, int page, String dataDir, int type, List<String> filePathList) throws IOException {
        String dataFilePath = "";
        DocType docType = DocTypeHelper.getDocType(path);
        String dataFileName = "";
        int pageIndex = page + 1;
        if (filePathList != null && filePathList.size() > 0) {
            switch (docType) {
                case PDF: {
                    if (type == 1) {
                        dataFileName = filePathList.stream().filter(x -> (x.equals(pageIndex + ".jpg") || x.equals(pageIndex + ".png"))).findFirst().orElse("");
                    } else {
                        dataFileName = filePathList.stream().filter(x -> (x.equals(pageIndex + ".html"))).findFirst().orElse("");
                    }
                    break;
                }
                case Excel: {
                    dataFileName = filePathList.stream().filter(x -> x.equals(page + ".html")).findFirst().orElse("");
                    break;
                }
                case Word: {
                    if (type == 1) {
                        dataFileName = filePathList.stream().filter(x -> x.equals(pageIndex + ".svg")).findFirst().orElse("");
                    } else {
                        dataFileName = filePathList.stream().filter(x -> x.equals(pageIndex + ".jpg") || (x.equals(pageIndex + ".png"))).findFirst().orElse("");
                    }
                    break;
                }
                case PPT: {
                    if (type == 1) {
                        dataFileName = filePathList.stream().filter(x -> x.equals(pageIndex + ".svg")).findFirst().orElse("");
                    } else {
                        dataFileName = filePathList.stream().filter(x -> x.equals(pageIndex + ".jpg") || (x.equals(pageIndex + ".png"))).findFirst().orElse("");
                    }
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
    @Synchronized
    public PreviewInfo initPreviewInfo(String ea, int employeeId, String path, String originalFilePath, String dataDir, long docSize, int pageCount, List<String> sheetNames) {
        PreviewInfo previewInfo = new PreviewInfo();
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
        return previewInfo;
    }

    @Override
    public PreviewInfo getInfoByPath(String ea, String path) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        return query.get();
    }
}
