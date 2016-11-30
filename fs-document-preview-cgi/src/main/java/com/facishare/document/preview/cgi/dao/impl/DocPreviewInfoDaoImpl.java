package com.facishare.document.preview.cgi.dao.impl;

import com.facishare.document.preview.cgi.convertor.ConvertorHelper;
import com.facishare.document.preview.cgi.dao.DocPreviewInfoDao;
import com.facishare.document.preview.cgi.model.DataFileInfo;
import com.facishare.document.preview.cgi.model.DocPreviewInfo;
import com.facishare.document.preview.cgi.utils.DateUtil;
import com.github.mongo.support.DatastoreExt;
import com.google.common.collect.Lists;
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
public class DocPreviewInfoDaoImpl implements DocPreviewInfoDao {


    private static final Logger logger = LoggerFactory.getLogger(DocPreviewInfoDaoImpl.class);

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public void saveDocPreviewInfo(String ea,String path,String dataFilePath,List<String> filePathList) throws IOException {
        if (filePathList == null) {
            filePathList = Lists.newArrayList();
        }
        logger.info("path:{}",filePathList);
        String dataFileName = FilenameUtils.getName(dataFilePath);
        filePathList.add(dataFileName);
        Query<DocPreviewInfo> query = dpsDataStore.createQuery(DocPreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        logger.info("query",query.toString());
        UpdateOperations<DocPreviewInfo> update = dpsDataStore.createUpdateOperations(DocPreviewInfo.class);
        update.set("filePathList", filePathList);
        dpsDataStore.findAndModify(query, update);
    }

    @Override
    public DataFileInfo getDataFileInfo( String ea,String path, int page,DocPreviewInfo docPreviewInfo) throws IOException {
        DataFileInfo dataFileInfo = new DataFileInfo();
        dataFileInfo.setOriginalFilePath(docPreviewInfo.getOriginalFilePath());
        dataFileInfo.setDataDir(docPreviewInfo.getDataDir());
        String fileExtension = FilenameUtils.getExtension(path).toLowerCase();
        String extension = fileExtension.contains("xls") ? ".html" : ".png";
        page = fileExtension.contains("ppt") ? page : page + 1;
        int finalPage = page;
        String dataFileName = docPreviewInfo.getFilePathList() == null || docPreviewInfo.getFilePathList().size() == 0 ? "" : docPreviewInfo.getFilePathList().stream().filter(x -> x.equals((finalPage) + extension)).findFirst().orElse("");
        if (!dataFileName.equals("")) {
            String filePath = docPreviewInfo.getDirName() + "/" + dataFileName;
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
    public DocPreviewInfo initDocPreviewInfo( String ea, int employeeId,String path, String originalFilePath, String dataDir, long docSize, int pageCount, List<String> sheetNames) {
        DocPreviewInfo docPreviewInfo = new DocPreviewInfo();
        docPreviewInfo.setDocSize(docSize);
        docPreviewInfo.setDirName(FilenameUtils.getBaseName(dataDir));
        docPreviewInfo.setCreateTime(new Date());
        int yyyyMMdd = Integer.parseInt(DateUtil.getFormatDateStr("yyyyMMdd"));
        docPreviewInfo.setCreateYYMMDD(yyyyMMdd);
        docPreviewInfo.setEa(ea);
        docPreviewInfo.setEmployeeId(employeeId);
        docPreviewInfo.setDataDir(dataDir);
        docPreviewInfo.setPath(path);
        docPreviewInfo.setSheetNames(sheetNames);
        docPreviewInfo.setPageCount(pageCount);
        docPreviewInfo.setOriginalFilePath(originalFilePath);
        List<String> filePathList = new ArrayList<>();
        docPreviewInfo.setFilePathList(filePathList);
        dpsDataStore.insert("DocPreviewInfo", docPreviewInfo);
        dpsDataStore.ensureIndexes();
        return docPreviewInfo;
    }

    @Override
    public DocPreviewInfo getInfoByPath(String ea,String path) {
        Query<DocPreviewInfo> query = dpsDataStore.createQuery(DocPreviewInfo.class);
        query.criteria("path").equal(path).criteria("ea").equal(ea);
        return query.get();
    }
}
