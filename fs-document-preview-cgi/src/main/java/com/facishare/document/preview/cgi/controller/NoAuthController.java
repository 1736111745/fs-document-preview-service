package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.model.DocPageResult;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.service.training.PreviewService;
import com.facishare.document.preview.cgi.utils.*;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.fxiaoke.common.Guard;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Splitter;
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
import java.util.List;

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
  @ReloadableProperty("previewKey")
  private String previewKey = "X8wh+9~+PKmG)b65";

  @ResponseBody
  @RequestMapping(value = "/preview/DocPreviewByPath", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public String docPreviewByPath(HttpServletRequest request) throws Exception {
    int pageCount = 0;
    String pathToken = UrlParametersHelper.safeGetRequestParameter(request, "path");
    String token = UrlParametersHelper.safeGetRequestParameter(request, "token");
    log.info("pathToken:{},token:{}", pathToken, token);
    if (Strings.isNullOrEmpty(pathToken) || Strings.isNullOrEmpty(token)) {
      return ResponseJsonHelper.getDocPreviewInfoResult(pathToken, pageCount);
    }
    String path = getPath(pathToken);
    log.info("path:{}", path);
    if (Strings.isNullOrEmpty(path)) {
      return ResponseJsonHelper.getDocPreviewInfoResult(pathToken, pageCount);
    }
    String extension = FilenameUtils.getExtension(path).toLowerCase();
    if (allowPreviewExtension.indexOf(extension) == -1) {
      return ResponseJsonHelper.getDocPreviewInfoResult(path, pageCount);
    }
    EmployeeInfo employeeInfo = getEmployeeInfo(token);
    log.info("employeeInfo:{}", com.alibaba.fastjson.JSON.toJSON(employeeInfo));
    if (employeeInfo == null) {
      return ResponseJsonHelper.getDocPreviewInfoResult(path, pageCount);
    }
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
    String pathToken = UrlParametersHelper.safeGetRequestParameter(request, "path");
    String token = UrlParametersHelper.safeGetRequestParameter(request, "token");
    int pageIndex = NumberUtils.toInt(UrlParametersHelper.safeGetRequestParameter(request, "pageIndex"), 0);
    log.info("pathToken:{},token:{}", pathToken, token);
    if (Strings.isNullOrEmpty(pathToken) || Strings.isNullOrEmpty(token)) {
      response.setStatus(400);
      return;
    }
    String path = getPath(pathToken);
    log.info("path:{}", path);
    if (Strings.isNullOrEmpty(path)) {
      response.setStatus(400);
      return;
    }
    EmployeeInfo employeeInfo = getEmployeeInfo(token);
    log.info("employeeInfo:{}", com.alibaba.fastjson.JSON.toJSON(employeeInfo));
    if (employeeInfo == null) {
      response.setStatus(400);
      return;
    }
    int width = NumberUtils.toInt(UrlParametersHelper.safeGetRequestParameter(request, "width"), 1024);
    width = width > 1920 ? 1920 : width;
    log.info("preview in no auth condition,path:{},employeeInfo:{},pageIndex:{}",path,com.alibaba.fastjson.JSON.toJSON(employeeInfo),pageIndex);
    DocPageResult result = previewService.getDocPage(employeeInfo, path, pageIndex);
    log.info("result:{}",com.alibaba.fastjson.JSON.toJSON(result));
    if (result.getCode() == 200) {
      fileOutPutor.outPut(response, result.getDataFilePath(), width, true);
    } else {
      response.setStatus(404);
      return;
    }
  }

  private String getPath(String token) {
    Guard guard = new Guard(previewKey);
    try {
      return guard.decode(token);
    } catch (Exception ex) {
      log.warn("unknown token,can't convert to path!token:{}", token, ex);
      return "";
    }
  }

  private EmployeeInfo getEmployeeInfo(String token) {
    Guard guard = new Guard(previewKey);
    try {
      String eaInfo = guard.decode(token);
      List<String> list = Splitter.on(".").trimResults().omitEmptyStrings().splitToList(eaInfo);
      if (list.size() == 3) {
        String ea = list.get(1);
        String employeeIdStr = list.get(2);
        if (NumberUtils.isNumber(employeeIdStr)) {
          int employeeId = NumberUtils.toInt(employeeIdStr);
          return EmployeeHelper.createEmployeeInfo(ea, employeeId);
        } else {
          return null;
        }
      } else {
        return null;
      }
    } catch (Exception ex) {
      log.warn("unknown token,can't convert to ea info!token:{}", token, ex);
      return null;
    }
  }
}
