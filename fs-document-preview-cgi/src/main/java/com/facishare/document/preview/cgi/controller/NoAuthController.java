package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.model.DocPageResult;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.service.training.PreviewService;
import com.facishare.document.preview.cgi.utils.*;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liuq on 2017/5/26.
 * 培训助手在微信下面的预览
 */
@RestController
@RequestMapping("/open/training")
@Slf4j
public class NoAuthController {

  @Autowired
  PreviewInfoHelper previewInfoHelper;
  @ReloadableProperty("allowPreviewExtension")
  private String allowPreviewExtension = "doc|docx|xls|xlsx|ppt|pptx|pdf|txt|csv";
  @Autowired
  PreviewService previewService;
  @Autowired
  FileOutPutor fileOutPutor;

  @ResponseBody
  @RequestMapping(value = "/preview/DocPreviewByPath", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public String docPreviewByPath(HttpServletRequest request) throws Exception {
    int pageCount = 0;
    String path = UrlParametersHelper.safeGetRequestParameter(request, "npath") == "" ?
      UrlParametersHelper.safeGetRequestParameter(request, "path") :
      UrlParametersHelper.safeGetRequestParameter(request, "path");
    String ea = UrlParametersHelper.safeGetRequestParameter(request, "ea");
    String employeeIdStr = UrlParametersHelper.safeGetRequestParameter(request, "employeeId");
    if (Strings.isNullOrEmpty(ea) || Strings.isNullOrEmpty(employeeIdStr) || !NumberUtils.isNumber(employeeIdStr)) {
      return ResponseJsonHelper.getDocPreviewInfoResult(path, pageCount);
    }
    if (!UrlParametersHelper.isValidPath(path)) {
      return ResponseJsonHelper.getDocPreviewInfoResult(path, pageCount);
    }
    String extension = FilenameUtils.getExtension(path).toLowerCase();
    if (allowPreviewExtension.indexOf(extension) == -1) {
      return ResponseJsonHelper.getDocPreviewInfoResult(path, pageCount);
    }
    int employeeId = NumberUtils.toInt(employeeIdStr);
    EmployeeInfo employeeInfo = EmployeeHelper.createEmployeeInfo(ea, employeeId);
    PreviewInfoEx previewInfoEx = previewInfoHelper.getPreviewInfo(employeeInfo, path, "");
    if (previewInfoEx.isSuccess()) {
      PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
      if (previewInfo != null) {
        pageCount = previewInfo.getPageCount();
      }
    }
    return ResponseJsonHelper.getDocPreviewInfoResult(path, pageCount);
  }

  @ResponseBody
  @RequestMapping(value = "/preview/DocPageByPath", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public void docPageByPath(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String path = UrlParametersHelper.safeGetRequestParameter(request, "npath") == "" ?
      UrlParametersHelper.safeGetRequestParameter(request, "path") :
      UrlParametersHelper.safeGetRequestParameter(request, "npath");
    String ea = UrlParametersHelper.safeGetRequestParameter(request, "ea");
    String employeeIdStr = UrlParametersHelper.safeGetRequestParameter(request, "employeeId");
    int pageIndex = NumberUtils.toInt(UrlParametersHelper.safeGetRequestParameter(request, "pageIndex"), 0);
    if (Strings.isNullOrEmpty(ea) || Strings.isNullOrEmpty(employeeIdStr) || !NumberUtils.isNumber(employeeIdStr)) {
      response.setStatus(400);
      return;
    }
    if (!UrlParametersHelper.isValidPath(path)) {
      response.setStatus(400);
      return;
    }
    int employeeId = NumberUtils.toInt(employeeIdStr);
    EmployeeInfo employeeInfo = EmployeeHelper.createEmployeeInfo(ea, employeeId);
    int width = NumberUtils.toInt(UrlParametersHelper.safeGetRequestParameter(request, "width"), 1024);
    width = width > 1920 ? 1920 : width;
    DocPageResult result = previewService.getDocPage(employeeInfo, path, pageIndex);
    if (result.getCode() == 200) {
      fileOutPutor.outPut(response, result.getDataFilePath(), width, true);
    }
  }
}
