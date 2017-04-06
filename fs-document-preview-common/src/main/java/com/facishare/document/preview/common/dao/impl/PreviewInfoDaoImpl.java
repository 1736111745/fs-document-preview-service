package com.facishare.document.preview.common.dao.impl;

import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.DocType;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.DateUtil;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import com.github.mongo.support.DatastoreExt;
import com.google.common.base.Strings;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
        update.add("filePathList", dataFileName);
        dpsDataStore.findAndModify(query, update);
    }

    @Override
    public String getDataFilePath(String path, int page, String dataDir, String filePath, int type,
                                  List<String> filePathList) throws IOException {
        //todo:优先去filePathList找，找不到去磁盘找一次，如果找到了就填充filePathList，原因是很多时候异步超时后，转换线程退出了，但转换进程还在工作。
        String dataFilePath = "";
        DocType docType = DocTypeHelper.getDocType(filePath);
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
    public PreviewInfo initPreviewInfo(String ea, int employeeId, String path, String originalFilePath, String pdfFilePath,
                                       String dataDir, long docSize, int pageCount, List<String> sheetNames) {
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
        previewInfo.setPdfFilePath(pdfFilePath);
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

    @Override
    public void savePdfFile(String ea, String path, String pdfFilePath) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
        update.set("pdfFilePath", pdfFilePath);
        dpsDataStore.findAndModify(query, update);
    }
}
