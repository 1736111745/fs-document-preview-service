package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.utils.EmployeeHelper;
import com.facishare.document.preview.cgi.utils.PreviewInfoHelper;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/restful")
@Slf4j
public class RestfulController {
  @Autowired
  private PreviewInfoHelper previewInfoHelper;
  @Autowired
  PreviewInfoDao previewInfoDao;

  /**
   * 获取文档页码数
   *
   * @param filePath   文件路径
   * @param ea         企业账号
   * @param employeeId 员工id
   * @return
   */
  @RequestMapping(value = "/document/getPageCount", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public String documentPageCount(String filePath, String ea, int employeeId) {
    String ret;
    try {
      Preconditions.checkNotNull(filePath, "filePath is null");
      Preconditions.checkNotNull(ea, "ea is null");
      Preconditions.checkNotNull(employeeId, "employeeId is null");
      EmployeeInfo employeeInfo = EmployeeHelper.createEmployeeInfo(ea, employeeId);
      PreviewInfoEx previewInfoEx = previewInfoHelper.getPreviewInfo(employeeInfo, filePath, "", 1000);
      Preconditions.checkNotNull(previewInfoEx.getPreviewInfo(), "document can't found!");
      ret = String.format("{\"pageCount\":%d}", previewInfoEx.getPreviewInfo().getPageCount());
    } catch (Exception e) {
      log.error("/document/getPageCount |filePath: {} | ea: {} | ei: {} ", filePath, ea, employeeId, e);
      ret = String.format("{\"error\":\"%s\"}", e.getMessage());
    }
    return ret;
  }

  /**
   * @param filePathList 文件路径
   * @param ea           企业账号
   * @return 删除预览文档
   */

  @RequestMapping(value = "/maintance/preview/clean", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
  public boolean documentPageClean(String ea,
                                   @RequestParam(required = false, value = "filePathList") List<String> filePathList) {
    List<PreviewInfo> previewInfoList = previewInfoDao.getInfoByPathList(ea, filePathList);
    if (previewInfoList == null) {
      return true;
    }
    for (PreviewInfo previewInfo : previewInfoList) {
      //获取文件夹实际路径
      String fileDir = previewInfo.getDataDir();
      File file = new File(fileDir);
      //递归删除，预防文件夹中还存在文件夹
      FileUtils.deleteQuietly(file);
    }
    //批量删除mongo数据
    previewInfoDao.patchClean(ea, filePathList);
    log.info("删除成功");
    return true;
  }

  /**
   * @param ea
   * @param filePathList
   * @return 查询预览文档
   */
  @RequestMapping(value = "/maintance/preview/query", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
  public List<PreviewInfo> documentPageView(String ea,
                                            @RequestParam(required = false, value = "filePathList") List<String> filePathList) {
    List<PreviewInfo> previewInfoList = previewInfoDao.getInfoByPathList(ea, filePathList);
    if (previewInfoList != null) {
      return previewInfoList;
    } else {
      return null;
    }
  }
}