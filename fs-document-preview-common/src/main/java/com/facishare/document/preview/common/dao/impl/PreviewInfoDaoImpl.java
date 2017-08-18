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
import org.mongodb.morphia.query.Criteria;
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
    if (path.startsWith("A_")) {
      query.criteria("path").equal(path);
    } else {
      query.criteria("path").equal(path).criteria("ea").equal(ea);
    }
    UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
    update.add("filePathList", dataFileName);
    dpsDataStore.findAndModify(query, update);
  }

  @Override
  public void updateDirection(String ea, String path, int direction) {
    Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
    if (path.startsWith("A_")) {
      query.criteria("path").equal(path);
    } else {
      query.criteria("path").equal(path).criteria("ea").equal(ea);
    }
    UpdateOperations<PreviewInfo> update = dpsDataStore.createUpdateOperations(PreviewInfo.class);
    update.set("direction", direction);
    dpsDataStore.findAndModify(query, update);
  }


  @Override
  public String getDataFilePath(String path,
                                int page,
                                String dataDir,
                                String filePath,
                                int type,
                                List<String> filePathList) throws IOException {
    String dataFilePath = "";
    DocType docType = DocTypeHelper.getDocType(filePath);
    String dataFileName = "";
    int pageIndex = page + 1;
    if (filePathList != null && filePathList.size() > 0) {
      switch (docType) {
        case PDF: {
          if (type == 1) {
            dataFileName = filePathList.stream().filter(x -> (x.equals(pageIndex + ".html"))).findFirst().orElse("");
          } else if (type == 2) {
            dataFileName = filePathList.stream()
                                       .filter(x -> (x.equals(pageIndex + ".jpg") || x.equals(pageIndex + ".png")))
                                       .findFirst()
                                       .orElse("");
          }
          break;
        }
        case Excel: {
          dataFileName = filePathList.stream().filter(x -> x.equals(page + ".html")).findFirst().orElse("");
          break;
        }
        case Word: {
          if (type == 1) {
            dataFileName = filePathList.stream().filter(x -> x.equals(pageIndex + ".html")).findFirst().orElse("");
          } else {
            dataFileName = filePathList.stream()
                                       .filter(x -> x.equals(pageIndex + ".jpg") || (x.equals(pageIndex + ".png")))
                                       .findFirst()
                                       .orElse("");
          }
          break;
        }
        case PPT: {
          if (type == 1) {
            dataFileName = filePathList.stream().filter(x -> x.equals(pageIndex + ".html")).findFirst().orElse("");
          } else {
            dataFileName = filePathList.stream()
                                       .filter(x -> x.equals(pageIndex + ".jpg") || (x.equals(pageIndex + ".png")))
                                       .findFirst()
                                       .orElse("");
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
    return previewInfo == null ? "" : previewInfo.getDataDir();
  }


  @Override
  @Synchronized
  public PreviewInfo initPreviewInfo(String ea,
                                     int employeeId,
                                     String path,
                                     String originalFilePath,
                                     String dataDir,
                                     long docSize,
                                     int pageCount,
                                     int direction,
                                     List<String> sheetNames,
                                     int width) {
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
    previewInfo.setDirection(direction);
    previewInfo.setSheetNames(sheetNames);
    previewInfo.setPageCount(pageCount);
    previewInfo.setWidth(width);
    previewInfo.setOriginalFilePath(originalFilePath);
    List<String> filePathList = new ArrayList<>();
    previewInfo.setFilePathList(filePathList);
    dpsDataStore.insert("PreviewInfo", previewInfo);
    dpsDataStore.ensureIndexes();
    return previewInfo;
  }

  @Override
  public PreviewInfo getInfoByPath(String ea, String path, int width) {
    log.info("getInfoByPath args,ea:{},path:{},width:{}", ea, path, width);
    Query<PreviewInfo> q1 = dpsDataStore.createQuery(PreviewInfo.class);
    Query<PreviewInfo> q2 = dpsDataStore.createQuery(PreviewInfo.class);
    Query<PreviewInfo> q3 = dpsDataStore.createQuery(PreviewInfo.class);
    if (width == 1000) {
      if (path.startsWith("A_")) {
        q1.criteria("path").equal(path).add(q2.criteria("width").equal(width).or(q3.criteria("width").doesNotExist()));
      } else {
        q1.criteria("path")
          .equal(path)
          .criteria("ea")
          .equal(ea)
          .add(q2.criteria("width").equal(width).or(q3.criteria("width").doesNotExist()));
      }

    } else {
      if (path.startsWith("A_")) {
        q1.criteria("path").equal(path).criteria("width").equal(width);
      } else {
        q1.criteria("path").equal(path).criteria("ea").equal(ea).criteria("width").equal(width);
      }
    }
    log.info("query:{}", q1.toString());
    return q1.get();
  }

  //批量删除预览记录
  @Override
  public void patchClean(String ea, List<String> pathList) {
    Query<PreviewInfo> query = dpsDataStore.createQuery(PreviewInfo.class);
    query.criteria("ea").equal(ea).criteria("path").in(pathList);
    dpsDataStore.delete(query);
  }

  //查询预览文档
  @Override
  public List<PreviewInfo> getInfoByPathList(String ea, List<String> pathList) {
    Query<PreviewInfo> q = dpsDataStore.createQuery(PreviewInfo.class);
    List<Criteria> criterias = new ArrayList<>();
    q.criteria("ea").equal(ea);
    for (String path : pathList) {
      String pathWithNoExt = FilenameUtils.removeExtension(path);
      criterias.add(q.criteria("path").contains(pathWithNoExt));
    }
    q.or(criterias.toArray(new Criteria[criterias.size()]));
    return q.asList();
  }
}