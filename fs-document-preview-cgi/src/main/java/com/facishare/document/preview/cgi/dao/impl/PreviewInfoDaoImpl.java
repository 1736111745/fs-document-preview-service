package com.facishare.document.preview.cgi.dao.impl;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.DataFileInfo;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.utils.DateUtil;
import com.github.mongo.support.DatastoreExt;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
@Repository
public class PreviewInfoDaoImpl implements PreviewInfoDao {

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public void savePreviewInfo(String ea,String path,String dataFilePath,List<String> filePathList) {
        String dataFileName = FilenameUtils.getName(dataFilePath);
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        if (filePathList == null) {
            filePathList = Lists.newArrayList();
        }
        filePathList.add(dataFileName);
        UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
        update.set("filePathList", filePathList);
        dpsDataStore.findAndModify(query, update);
    }

    @Override
    public DataFileInfo getDataFileInfo( String ea,String path, int page,PreviewInfo previewInfo) throws IOException {
        DataFileInfo dataFileInfo = new DataFileInfo();
        dataFileInfo.setOriginalFilePath(previewInfo.getOriginalFilePath());
        dataFileInfo.setDataDir(previewInfo.getDataDir());
        String fileExtension = FilenameUtils.getExtension(path).toLowerCase();
        String dataFileName = "";
        int pageIndex = page + 1;
        List<String> filePathList = previewInfo.getFilePathList();
        if (filePathList != null && filePathList.size() > 0) {
            switch (fileExtension) {
                case "pdf": {
                    dataFileName = filePathList.stream().filter(x -> (x.equals(pageIndex + ".jpg") || x.equals(pageIndex + ".png"))).findFirst().orElse("");
                    break;
                }
                case "xls": {
                    dataFileName = filePathList.stream().filter(x -> x.equals(pageIndex + ".html")).findFirst().orElse("");
                    break;
                }
                default: {
                    dataFileName = filePathList.stream().filter(x -> x.equals(pageIndex + ".html")).findFirst().orElse("");
                    break;
                }
            }
        }
        String filePath="";
        if (!Strings.isNullOrEmpty(dataFileName)) {
            filePath = FilenameUtils.concat(previewInfo.getDirName(), dataFileName);
        }
        dataFileInfo.setShortFilePath(filePath);
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
    public PreviewInfo initPreviewInfo( String ea, int employeeId,String path, String originalFilePath, String dataDir, long docSize, int pageCount, List<String> sheetNames) {
        PreviewInfo previewInfo = new PreviewInfo();
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
        return previewInfo;
    }

    @Override
    public PreviewInfo getInfoByPath(String ea,String path) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        return query.get();
    }
}
