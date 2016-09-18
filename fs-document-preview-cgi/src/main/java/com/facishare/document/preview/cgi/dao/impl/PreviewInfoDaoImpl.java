package com.facishare.document.preview.cgi.dao.impl;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.model.DataFileInfo;
import com.facishare.document.preview.cgi.utils.DateUtil;
import com.github.mongo.support.DatastoreExt;
import org.apache.commons.io.FilenameUtils;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuq on 16/8/16.
 */
@Repository
public class PreviewInfoDaoImpl implements PreviewInfoDao {

    private static final Logger LOG = LoggerFactory.getLogger(PreviewInfoDaoImpl.class);

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public void create(String path, String baseDir, String dataFilePath, String ea, int employeeId, long docSize, int pageCount) throws IOException {
        String dataFileName = FilenameUtils.getName(dataFilePath);
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path);
        PreviewInfo previewInfo = query.get();
        if (previewInfo == null) {
            previewInfo = new PreviewInfo();
            previewInfo.setDocSize(docSize);
            previewInfo.setDirName(FilenameUtils.getBaseName(baseDir));
            previewInfo.setCreateTime(new Date());
            int yyyyMMdd = Integer.parseInt(DateUtil.getFormatDateStr("yyyyMMdd"));
            previewInfo.setCreateYYMMDD(yyyyMMdd);
            previewInfo.setEa(ea);
            previewInfo.setEmployeeId(employeeId);
            previewInfo.setDataDir(baseDir);
            previewInfo.setPath(path);
            previewInfo.setPageCount(pageCount);
            List<String> svgs = new ArrayList<>();
            svgs.add(dataFileName);
            previewInfo.setFilePathList(svgs);
            dpsDataStore.insert("PreviewInfo", previewInfo);
            dpsDataStore.ensureIndexes();
        } else {
            List<String> filePathList = previewInfo.getFilePathList();
            if (filePathList == null) {
                filePathList = new ArrayList<>();
                filePathList.add(dataFileName);
                previewInfo.setFilePathList(filePathList);
                UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
                update.set("filePathList", filePathList);
                dpsDataStore.findAndModify(query, update);
            } else {
                if (!filePathList.contains(dataFileName)) {
                    filePathList.add(dataFileName);
                    previewInfo.setFilePathList(filePathList);
                    UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
                    update.set("filePathList", filePathList);
                    dpsDataStore.findAndModify(query, update);
                }
            }
        }
    }

    @Override
    public DataFileInfo getDataFileInfo(String path, int page, String ea) throws IOException {
        DataFileInfo dataFileInfo = new DataFileInfo();
        PreviewInfo previewInfo = getInfoByPath(path);
        dataFileInfo.setOriginalFilePath(previewInfo.getOriginalFilePath());
        dataFileInfo.setDataDir(previewInfo.getDataDir());
        String extension = FilenameUtils.getExtension(path).toLowerCase().equals("pdf") ? ".png" : ".jpg";
        String dataFileName = previewInfo.getFilePathList() == null || previewInfo.getFilePathList().size() == 0 ? "" : previewInfo.getFilePathList().stream().filter(x -> x.equals((page + 1) + extension)).findFirst().orElse("");
        if (!dataFileName.equals("")) {
            String filePath = previewInfo.getDirName() + "/" + dataFileName;
            dataFileInfo.setShortFilePath(filePath);
        } else {
            dataFileInfo.setShortFilePath("");
        }
        return dataFileInfo;
    }

    @Override
    public String getBaseDir(String dirName) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("dirName").equal(dirName);
        PreviewInfo previewInfo = query.get();
        return previewInfo.getDataDir();
    }


    @Override
    public void initPreviewInfo(String path, String originalFilePath, String dataDir, long docSize, int pageCount, String ea, int employeeId) {
        PreviewInfo previewInfo = getInfoByPath(path);
        if (previewInfo == null) {
            previewInfo = new PreviewInfo();
            previewInfo.setDocSize(docSize);
            previewInfo.setDirName(FilenameUtils.getBaseName(dataDir));
            previewInfo.setCreateTime(new Date());
            int yyyyMMdd = Integer.parseInt(DateUtil.getFormatDateStr("yyyyMMdd"));
            previewInfo.setCreateYYMMDD(yyyyMMdd);
            previewInfo.setEa(ea);
            previewInfo.setEmployeeId(employeeId);
            previewInfo.setDataDir(dataDir);
            previewInfo.setPath(path);
            previewInfo.setPageCount(pageCount);
            previewInfo.setOriginalFilePath(originalFilePath);
            List<String> filePathList = new ArrayList<>();
            previewInfo.setFilePathList(filePathList);
            dpsDataStore.insert("PreviewInfo", previewInfo);
            dpsDataStore.ensureIndexes();
        }
    }

    @Override
    public PreviewInfo getInfoByPath(String path) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path);
        return query.get();
    }
}
