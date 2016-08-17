package com.facishare.document.preview.cgi.dao.impl;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.utils.PathHelper;
import com.github.mongo.support.DatastoreExt;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.mongodb.morphia.query.Query;
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

    @Autowired
    private DatastoreExt datastoreExt;

    @Override
    public void create(String path,String filePath, String ea, int employeeId,long docSize) {
        PreviewInfo previewInfo = new PreviewInfo();
        String htmlName = FilenameUtils.getBaseName(filePath);
        File file=new File(filePath);
        previewInfo.setDocSize(docSize);
        File htmlDir=new File(file.getParent());
        BigInteger dirHtmlLength=FileUtils.sizeOfDirectoryAsBigInteger(htmlDir);
        previewInfo.setHtmlSize(dirHtmlLength.longValue()+file.length());
        previewInfo.setHtmlName(htmlName);
        previewInfo.setCreateTime(new Date());
        int yyyyMMdd = Integer.parseInt(PathHelper.getFormatDateStr("yyyyMMdd"));
        previewInfo.setCreateYYMMDD(yyyyMMdd);
        previewInfo.setEa(ea);
        previewInfo.setEmployeeId(employeeId);
        previewInfo.setHtmlFilePath(filePath);
        previewInfo.setPath(path);
        datastoreExt.insert("PreviewInfo", previewInfo);
        datastoreExt.ensureIndexes();
    }

    @Override
    public PreviewInfo getInfo(String condition,int type) {
        Query<PreviewInfo> query = datastoreExt.createQuery(PreviewInfo.class);
        if (type == 0)
            query.criteria("htmlName").equal(condition);
        else
            query.criteria("path").equals(condition);
        return query.get();
    }
}
