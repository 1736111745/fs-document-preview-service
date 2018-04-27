package com.facishare.document.preview.cgi.service.training;

import com.facishare.document.preview.cgi.model.DocPageResult;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.cgi.utils.PreviewInfoHelper;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.OfficeApiHelper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by liuq on 2017/5/26.
 */
@Slf4j
@Service
public class PreviewService {

  @Autowired
  PreviewInfoDao previewInfoDao;
  @Autowired
  PreviewInfoHelper previewService;
  @Autowired
  OfficeApiHelper officeApiHelper;
  @Autowired
  FileStorageProxy fileStorageProxy;

  public DocPageResult getDocPage(EmployeeInfo employeeInfo, String path, int pageIndex) {
    log.info("begin get doc page,employeeInfo:{},path:{},pageIndex:{}", com.alibaba.fastjson.JSON.toJSON(employeeInfo), path, pageIndex);
    DocPageResult result = new DocPageResult();
    try {
      PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, "", 1000);
      log.info("path:{},previewInfoEx:{}", path, com.alibaba.fastjson.JSON.toJSON(previewInfoEx));
      if (!previewInfoEx.isSuccess()) {
        log.warn("can't get previewInfo,path:{},pageIndex:{}", path, pageIndex);
        result.setCode(404);
      } else {
        PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
        if (previewInfo == null) {
          log.warn("can't resolve path:{},page:{}", path, pageIndex);
          result.setCode(404);
        } else {
          if (pageIndex >= previewInfo.getPageCount()) {
            log.warn("invalid page,path:{},page:{}", path, pageIndex);
            result.setCode(400);
          } else {
            String dataFilePath = previewInfoDao.getDataFilePath(path, pageIndex, previewInfo.getDataDir(), previewInfo.getOriginalFilePath(), 2, previewInfo
              .getFilePathList());
            if (!Strings.isNullOrEmpty(dataFilePath) && new File(dataFilePath).exists()) {
              result.setCode(200);
              result.setDataFilePath(dataFilePath);
            } else {
              String originalFilePath = previewInfo.getOriginalFilePath();
              File originalFile = new File(originalFilePath);
              if (!originalFile.exists()) {
                fileStorageProxy.DownloadAndSave(path, employeeInfo.getEa(), employeeInfo.getEmployeeId(), "", originalFilePath);
              }
              boolean apiResult = officeApiHelper.convertOffice2Png(originalFilePath, pageIndex);
              if (apiResult) {
                dataFilePath =
                  FilenameUtils.getFullPathNoEndSeparator(originalFilePath) + "/" + (pageIndex + 1) + ".png";
                log.info("dataFilePath:{}", dataFilePath);
                previewInfoDao.savePreviewInfo(employeeInfo.getEa(), path, dataFilePath, 1000);
                result.setCode(200);
                result.setDataFilePath(dataFilePath);
              } else {
                log.warn("can't resolve path:{},page:{}", path, pageIndex);
                result.setCode(404);
              }
            }
          }
        }
      }
    } catch (Exception e) {
      log.warn("can't get previewInfo,path:{},pageIndex:{}", path, pageIndex, e);
      result.setCode(404);
    }
    return result;
  }
}
