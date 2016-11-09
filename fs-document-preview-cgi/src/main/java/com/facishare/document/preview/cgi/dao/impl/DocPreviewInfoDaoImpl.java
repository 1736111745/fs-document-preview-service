package com.facishare.document.preview.cgi.dao.impl;

import com.facishare.document.preview.cgi.dao.DocPreviewInfoDao;
import com.facishare.document.preview.cgi.model.DataFileInfo;
import com.facishare.document.preview.cgi.model.DocPreviewInfo;
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
public class DocPreviewInfoDaoImpl implements DocPreviewInfoDao {

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public void saveDocPreviewInfo(String ea,String path,String dataFilePath) throws IOException {
        String dataFileName = FilenameUtils.getName(dataFilePath);
        Query<DocPreviewInfo> query = dpsDataStore.createQuery(DocPreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        DocPreviewInfo DocPreviewInfo = query.get();
        List<String> filePathList = DocPreviewInfo.getFilePathList();
        if (filePathList == null) {
            filePathList = new ArrayList<>();
            filePathList.add(dataFileName);
            DocPreviewInfo.setFilePathList(filePathList);
            UpdateOperations<DocPreviewInfo> update = dpsDataStore.createUpdateOperations(DocPreviewInfo.class);
            update.set("filePathList", filePathList);
            dpsDataStore.findAndModify(query, update);
        } else {
            if (!filePathList.contains(dataFileName)) {
                filePathList.add(dataFileName);
                DocPreviewInfo.setFilePathList(filePathList);
                UpdateOperations<DocPreviewInfo> update = dpsDataStore.createUpdateOperations(DocPreviewInfo.class);
                update.set("filePathList", filePathList);
                dpsDataStore.findAndModify(query, update);
            }
        }
    }

    @Override
    public DataFileInfo getDataFileInfo( String ea,String path, int page,DocPreviewInfo DocPreviewInfo) throws IOException {
        DataFileInfo dataFileInfo = new DataFileInfo();
        dataFileInfo.setOriginalFilePath(DocPreviewInfo.getOriginalFilePath());
        dataFileInfo.setDataDir(DocPreviewInfo.getDataDir());
        String fileExtension = FilenameUtils.getExtension(path).toLowerCase();
        String extension = fileExtension.contains("xls") ? ".html" : ".png";
        page = fileExtension.contains("ppt") ? page : page + 1;
        int finalPage = page;
        String dataFileName = DocPreviewInfo.getFilePathList() == null || DocPreviewInfo.getFilePathList().size() == 0 ? "" : DocPreviewInfo.getFilePathList().stream().filter(x -> x.equals((finalPage) + extension)).findFirst().orElse("");
        if (!dataFileName.equals("")) {
            String filePath = DocPreviewInfo.getDirName() + "/" + dataFileName;
            dataFileInfo.setShortFilePath(filePath);
        } else {
            dataFileInfo.setShortFilePath("");
        }
        return dataFileInfo;
    }

    @Override
    public String getBaseDir(String dirName) {
        Query<DocPreviewInfo> query = dpsDataStore.createQuery(DocPreviewInfo.class);
        query.criteria("dirName").equal(dirName);
        DocPreviewInfo DocPreviewInfo = query.get();
        return DocPreviewInfo.getDataDir();
    }


    @Override
    public void initDocPreviewInfo( String ea, int employeeId,String path, String originalFilePath, String dataDir, long docSize, int pageCount, List<String> sheetNames)
    {
        DocPreviewInfo DocPreviewInfo = getInfoByPath(ea,path);
        if (DocPreviewInfo == null) {
            DocPreviewInfo = new DocPreviewInfo();
            DocPreviewInfo.setDocSize(docSize);
            DocPreviewInfo.setDirName(FilenameUtils.getBaseName(dataDir));
            DocPreviewInfo.setCreateTime(new Date());
            int yyyyMMdd = Integer.parseInt(DateUtil.getFormatDateStr("yyyyMMdd"));
            DocPreviewInfo.setCreateYYMMDD(yyyyMMdd);
            DocPreviewInfo.setEa(ea);
            DocPreviewInfo.setEmployeeId(employeeId);
            DocPreviewInfo.setDataDir(dataDir);
            DocPreviewInfo.setPath(path);
            DocPreviewInfo.setSheetNames(sheetNames);
            DocPreviewInfo.setPageCount(pageCount);
            DocPreviewInfo.setOriginalFilePath(originalFilePath);
            List<String> filePathList = new ArrayList<>();
            DocPreviewInfo.setFilePathList(filePathList);
            dpsDataStore.insert("DocPreviewInfo", DocPreviewInfo);
            dpsDataStore.ensureIndexes();
        }
    }

    @Override
    public DocPreviewInfo getInfoByPath(String ea,String path) {
        Query<DocPreviewInfo> query = dpsDataStore.createQuery(DocPreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        return query.get();
    }
}
