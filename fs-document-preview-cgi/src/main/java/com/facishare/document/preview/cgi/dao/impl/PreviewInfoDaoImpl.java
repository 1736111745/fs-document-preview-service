package com.facishare.document.preview.cgi.dao.impl;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.model.SvgFileInfo;
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
    public void create(String path, String baseDir, String svgFilePath, String ea, int employeeId, long docSize) throws IOException {
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
            List<String> svgs = new ArrayList<>();
            svgs.add(svgFileName);
            previewInfo.setSvgList(svgs);
            dpsDataStore.insert("PreviewInfo", previewInfo);
            dpsDataStore.ensureIndexes();
        } else {
            List<String> svgs = previewInfo.getSvgList();
            if (svgs == null) {
                svgs = new ArrayList<>();
                svgs.add(svgFileName);
                previewInfo.setSvgList(svgs);
                UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
                update.set("svgList", svgs);
                dpsDataStore.findAndModify(query, update);
            } else {
                if (!svgs.contains(svgFileName)) {
                    svgs.add(svgFileName);
                    previewInfo.setSvgList(svgs);
                    UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
                    update.set("svgList", svgs);
                    dpsDataStore.findAndModify(query, update);
                }
            }
        }
    }

    @Override
    public SvgFileInfo getSvgBaseDir(String path, int page, String ea) throws IOException {
        SvgFileInfo svgFileInfo = new SvgFileInfo();
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path);
        PreviewInfo previewInfo = getInfoByPath(path);
        if (previewInfo != null) {
            String baseDir = previewInfo.getBaseDir();
            svgFileInfo.setBaseDir(baseDir);
            String svgFileName = previewInfo.getSvgList().stream().filter(x -> x.equals((page + 1) + ".svg")).findFirst().orElse("");
            if (!svgFileName.equals("")) {
                String filePath = previewInfo.getFolderName() + "/" + svgFileName;
                svgFileInfo.setFilePath(filePath);
            } else {
                svgFileInfo.setFilePath("");
            }
        } else {
            String baseDir = new PathHelper(ea).getSvgFolder(path);
            svgFileInfo.setBaseDir(baseDir);
            svgFileInfo.setFilePath("");
        }
        return svgFileInfo;
    }

    @Override
    public String getSvgBaseDir(String folderName) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("folderName").equal(folderName);
        PreviewInfo previewInfo= query.get();
        return previewInfo.getBaseDir();
    }

    public PreviewInfo getInfoByPath(String path) {
        Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
        query.criteria("path").equal(path);
        return query.get();
    }
}
