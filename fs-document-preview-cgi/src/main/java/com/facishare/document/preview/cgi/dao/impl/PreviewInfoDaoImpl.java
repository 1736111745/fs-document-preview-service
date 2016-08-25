package com.facishare.document.preview.cgi.dao.impl;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.utils.PathHelper;
import com.github.mongo.support.DatastoreExt;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by liuq on 16/8/16.
 */
@Repository
public class PreviewInfoDaoImpl implements PreviewInfoDao {

    private static final Logger LOG = LoggerFactory.getLogger(PreviewInfoDaoImpl.class);

    @Autowired
    private DatastoreExt dpsDataStore;

    @Override
    public void create(String path, String filePath, String ea, int employeeId, long docSize) {
        PreviewInfo previewInfo = new PreviewInfo();
        String htmlName = FilenameUtils.getBaseName(filePath);
        File file = new File(filePath);
        previewInfo.setDocSize(docSize);
        File htmlDir = new File(file.getParent());
        BigInteger dirHtmlLength = FileUtils.sizeOfDirectoryAsBigInteger(htmlDir);
        previewInfo.setHtmlSize(dirHtmlLength.longValue() + file.length());
        previewInfo.setHtmlName(htmlName);
        previewInfo.setCreateTime(new Date());
        int yyyyMMdd = Integer.parseInt(PathHelper.getFormatDateStr("yyyyMMdd"));
        previewInfo.setCreateYYMMDD(yyyyMMdd);
        previewInfo.setEa(ea);
        previewInfo.setEmployeeId(employeeId);
        previewInfo.setHtmlFilePath(filePath);
        previewInfo.setPath(path);
        dpsDataStore.insert("PreviewInfo", previewInfo);
        dpsDataStore.ensureIndexes();
    }

    @Override
    public PreviewInfo getInfoByPath(String path) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path);
        return query.get();
    }

    @Override
    public PreviewInfo getInfoByHtmlName(String htmlName) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("htmlName").equal(htmlName);
        return query.get();
    }

}
