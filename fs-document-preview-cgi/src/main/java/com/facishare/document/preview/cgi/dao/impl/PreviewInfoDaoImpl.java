package com.facishare.document.preview.cgi.dao.impl;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.DataFileInfo;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.utils.DateUtil;
import com.github.mongo.support.DatastoreExt;
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
 * Created by liuq on 16/8/16.
 */
@Repository
public class PreviewInfoDaoImpl implements PreviewInfoDao {

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public void savePreviewInfo(String ea,String path,String dataFilePath) throws IOException {
        String dataFileName = FilenameUtils.getName(dataFilePath);
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        PreviewInfo previewInfo = query.get();
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

    @Override
    public DataFileInfo getDataFileInfo( String ea,String path, int page,PreviewInfo previewInfo) throws IOException {
        DataFileInfo dataFileInfo = new DataFileInfo();
        dataFileInfo.setOriginalFilePath(previewInfo.getOriginalFilePath());
        dataFileInfo.setDataDir(previewInfo.getDataDir());
        String fileExtension = FilenameUtils.getExtension(path).toLowerCase();
        String extension = fileExtension.equals("pdf") ? ".png" : fileExtension.contains("xls") ? ".html" : ".svg";
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
    public void initPreviewInfo( String ea, int employeeId,String path, String originalFilePath, String dataDir, long docSize, int pageCount, List<String> sheetNames)
    {
        PreviewInfo previewInfo = getInfoByPath(ea,path);
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
            previewInfo.setSheetNames(sheetNames);
            previewInfo.setPageCount(pageCount);
            previewInfo.setOriginalFilePath(originalFilePath);
            List<String> filePathList = new ArrayList<>();
            previewInfo.setFilePathList(filePathList);
            dpsDataStore.insert("PreviewInfo", previewInfo);
            dpsDataStore.ensureIndexes();
        }
    }

    @Override
    public PreviewInfo getInfoByPath(String ea,String path) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        return query.get();
    }
}
