package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertOldOfficeVersionResult;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.OfficeApiHelper;
import com.facishare.document.preview.common.utils.PathHelper;
import com.facishare.document.preview.common.utils.SampleUUID;
import com.github.autoconf.ConfigFactory;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

/**
 * Created by liuq on 2016/11/23.
 */
@Slf4j
@Component
public class PreviewInfoHelper {
  @Autowired
  FileStorageProxy fileStorageProxy;
  @Autowired
  PreviewInfoDao previewInfoDao;
  @Autowired
  OfficeApiHelper officeApiHelper;
  private String redirectPreviewExtension = "txt|sql|js|css|json|csv|svg|webp|jpg|png|bmp|gif|jpeg|mp4";
  private int previewMaxPagCount = 200;
  private List<String> pdf2ImageMd5List = Lists.newArrayList();

  @PostConstruct
  public void init() {
    ConfigFactory.getInstance().getConfig("fs-dps-config", config -> {
      String pdf2ImageMd5s = config.get("pdf2ImageMd5s");
      pdf2ImageMd5List = Splitter.on('|').trimResults().omitEmptyStrings().splitToList(pdf2ImageMd5s);
      previewMaxPagCount = config.getInt("previewMaxPagCount");
      redirectPreviewExtension = config.get("redirectPreviewExtension");
    });
  }

  /**
   * 手机预览
   *
   * @param employeeInfo
   * @param npath
   * @param securityGroup
   * @return
   */
  public PreviewInfoEx getPreviewInfo(EmployeeInfo employeeInfo, String npath, String securityGroup, int width) {
    String ea = employeeInfo.getEa();
    int employeeId = employeeInfo.getEmployeeId();
    PreviewInfoEx previewInfoEx = new PreviewInfoEx();
    try {
      PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, npath, width);
      int pageCount;
      List<String> sheetNames;
      String extension = FilenameUtils.getExtension(npath).toLowerCase();
      PageInfo pageInfo = new PageInfo();
      if (previewInfo == null) {
        log.info("preview info is null!path:{}", npath);
        byte[] bytes = fileStorageProxy.GetBytesByPath(npath, ea, employeeId, securityGroup);
        if (bytes != null && bytes.length > 0) {
          if (extension.contains("xls") && bytes.length > 1024 * 1024 * 20) {
            previewInfoEx.setSuccess(false);
            previewInfoEx.setPreviewInfo(null);
            previewInfoEx.setErrorMsg("excel文件大于20M，不支持预览！");
          } else {
            if (bytes.length > 1024 * 1024 * 100) {
              previewInfoEx.setSuccess(false);
              previewInfoEx.setPreviewInfo(null);
              previewInfoEx.setErrorMsg("当前文件大于100M，不支持预览！");
            } else {
              String dataDir = new PathHelper(ea).getDataDir();
              String fileName = SampleUUID.getUUID() + "." + extension;
              String filePath = FilenameUtils.concat(dataDir, fileName);
              FileUtils.writeByteArrayToFile(new File(filePath), bytes);
              log.info("save file to {},npath:{}", filePath, npath);
              if (redirectPreviewExtension.indexOf(extension) > -1) {
                pageInfo.setSuccess(true);
                pageInfo.setPageCount(1);
              } else {
                //旧版本office格式先转换为新版本office格式
                if (extension.equals("xls") || extension.equals("doc") || extension.equals("ppt")) {
                  ConvertOldOfficeVersionResult result = officeApiHelper.convertFile(filePath);
                  if (result != null && result.isSuccess()) {
                    filePath = result.getNewFilePath();
                  }
                }
                pageInfo = officeApiHelper.getPageInfo(npath, filePath);
              }
              if (pageInfo.getPageCount() > previewMaxPagCount) {
                previewInfoEx.setSuccess(false);
                previewInfoEx.setPreviewInfo(null);
                previewInfoEx.setErrorMsg("当前文件页码数超过" + previewMaxPagCount + "页，不支持预览！");
              } else {
                if (pageInfo.isSuccess()) {
                  pageCount = pageInfo.getPageCount();
                  sheetNames = pageInfo.getSheetNames();
                  int pdfConvertType = 0;
                  if (extension.equals("pdf")) {
                    String fileMd5 = MD5Helper.getMd5ByFile(new File(filePath));
                    if (pdf2ImageMd5List.size() > 0 && pdf2ImageMd5List.contains(fileMd5)) {
                      pdfConvertType = 1;
                    }
                  }
                  previewInfo = previewInfoDao.initPreviewInfo(ea, employeeId, npath, filePath, dataDir, bytes.length, pageCount, sheetNames, width, pdfConvertType);
                  previewInfoEx.setSuccess(true);
                  previewInfoEx.setPreviewInfo(previewInfo);
                } else {
                  previewInfoEx.setSuccess(false);
                  previewInfoEx.setPreviewInfo(null);
                  previewInfoEx.setErrorMsg(pageInfo.getErrorMsg());
                }
              }
            }
          }
        } else {
          previewInfoEx.setSuccess(false);
          previewInfoEx.setPreviewInfo(null);
          previewInfoEx.setErrorMsg("该文档无法预览!");
          log.warn("npath:{} can't been download!", npath);
        }
      } else {
        previewInfoEx.setSuccess(true);
        previewInfoEx.setPreviewInfo(previewInfo);
      }
    } catch (Exception e) {
      log.error("getPreviewInfo happened exception!,npath:{}", npath, e);
    }
    return previewInfoEx;
  }
}
