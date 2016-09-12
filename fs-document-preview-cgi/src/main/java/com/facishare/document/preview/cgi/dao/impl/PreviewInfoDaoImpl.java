package com.facishare.document.preview.cgi.dao.impl;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.model.DataFileInfo;
import com.facishare.document.preview.cgi.utils.DateUtil;
import com.facishare.document.preview.cgi.utils.PathHelper;
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
    public void create(String path, String baseDir, String svgFilePath, String ea, int employeeId, long docSize,int pageCount) throws IOException {
        String svgFileName = FilenameUtils.getName(svgFilePath);
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path);
        PreviewInfo previewInfo = query.get();
        if (previewInfo == null) {
            previewInfo = new PreviewInfo();
            previewInfo.setDocSize(docSize);
            previewInfo.setFolderName(FilenameUtils.getBaseName(baseDir));
            previewInfo.setCreateTime(new Date());
            int yyyyMMdd = Integer.parseInt(DateUtil.getFormatDateStr("yyyyMMdd"));
            previewInfo.setCreateYYMMDD(yyyyMMdd);
            previewInfo.setEa(ea);
            previewInfo.setEmployeeId(employeeId);
            previewInfo.setBaseDir(baseDir);
            previewInfo.setPath(path);
            previewInfo.setPageCount(pageCount);
            List<String> svgs = new ArrayList<>();
            svgs.add(svgFileName);
            previewInfo.setFilePathList(svgs);
            dpsDataStore.insert("PreviewInfo", previewInfo);
            dpsDataStore.ensureIndexes();
        } else {
            List<String> filePathList = previewInfo.getFilePathList();
            if (filePathList == null) {
                filePathList = new ArrayList<>();
                filePathList.add(svgFileName);
                previewInfo.setFilePathList(filePathList);
                UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
                update.set("filePathList", filePathList);
                dpsDataStore.findAndModify(query, update);
            } else {
                if (!filePathList.contains(svgFileName)) {
                    filePathList.add(svgFileName);
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
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path);
        PreviewInfo previewInfo = getInfoByPath(path);
        if (previewInfo != null) {
            String baseDir = previewInfo.getBaseDir();
            dataFileInfo.setBaseDir(baseDir);
            String extension = FilenameUtils.getExtension(path).toLowerCase().equals("pdf") ? ".png" : ".svg";
            String dataFileName = previewInfo.getFilePathList().stream().filter(x -> x.equals((page + 1) + extension)).findFirst().orElse("");
            if (!dataFileName.equals("")) {
                String filePath = previewInfo.getFolderName() + "/" + dataFileName;
                dataFileInfo.setFilePath(filePath);
            } else {
                dataFileInfo.setFilePath("");
            }
        } else {
            String baseDir = new PathHelper(ea).getDataFolder();
            dataFileInfo.setBaseDir(baseDir);
            dataFileInfo.setFilePath("");
        }
        return dataFileInfo;
    }

    @Override
    public String getDataFileInfo(String folderName) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("folderName").equal(folderName);
        PreviewInfo previewInfo= query.get();
        return previewInfo.getBaseDir();
    }

    @Override
    public int getPageCount(String path) {
        PreviewInfo previewInfo = getInfoByPath(path);
        if (previewInfo == null) {
            return 0;
        } else
            return previewInfo.getPageCount();
    }

    private PreviewInfo getInfoByPath(String path) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path);
        return query.get();
    }
}
