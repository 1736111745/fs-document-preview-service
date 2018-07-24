package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.cgi.model.DocPageResult;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.model.ShareTokenParamInfo;
import com.facishare.document.preview.cgi.service.training.PreviewService;
import com.facishare.document.preview.cgi.utils.*;
import com.facishare.document.preview.common.dao.FileTokenDao;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.DownloadFileTokens;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.ConvertOffice2PdfEnqueueUtil;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import com.facishare.document.preview.common.utils.OfficeApiHelper;
import com.fxiaoke.metrics.CounterService;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by liuq on 16/8/5.
 */
@Slf4j
@Controller
@RequestMapping("/")
public class PreviewController {

  @Autowired
  PreviewInfoDao previewInfoDao;
  @Autowired
  FileTokenDao fileTokenDao;
  @Autowired
  PreviewInfoHelper previewInfoHelper;
  @Autowired
  CounterService counterService;
  @Autowired
  ConvertOffice2PdfEnqueueUtil convertOffice2PdfEnqueueUtil;
  @Autowired
  OfficeApiHelper officeApiHelper;
  @Autowired
  FileOutPutor fileOutPutor;
  @Autowired
  PreviewService previewService;
  @ReloadableProperty("allowPreviewExtension")
  private String allowPreviewExtension = "doc|docx|xls|xlsx|ppt|pptx|pdf|txt|csv|webp";
  @ReloadableProperty("htmlWidthList")
  private String htmlWidthList = "1000|640";

  @ResponseBody
  @RequestMapping(value = "/preview/getPreviewInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public String getPreviewInfo(HttpServletRequest request) {
    String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
    String token = UrlParametersHelper.safeGetRequestParameter(request, "token");

    EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
    int width = NumberUtils.toInt(UrlParametersHelper.safeGetRequestParameter(request, "width"), 1000);
    String securityGroup = UrlParametersHelper.safeGetRequestParameter(request, "sg");
    if (path.equals("") && token.equals("")) {
      return ResponseJsonHelper.getPreviewInfoResult("参数错误!");
    }
    if (!token.equals("")) {
      log.info("getTokenInfo,ea:{},token:{},sourceUser:{}", employeeInfo.getEa(), token, employeeInfo.getSourceUser());
      DownloadFileTokens fileToken = fileTokenDao.getInfo(employeeInfo.getEa(), token, employeeInfo.getSourceUser());
      if (fileToken != null && fileToken.getFileType().toLowerCase().equals("preview")) {
        {
          path = Strings.isNullOrEmpty(fileToken.getFilePath()) ? "" : fileToken.getFilePath().trim();
          securityGroup = Strings.isNullOrEmpty(fileToken.getDownloadSecurityGroup()) ?
            "" :
            fileToken.getDownloadSecurityGroup().trim();
        }
      }
    }
    if (!UrlParametersHelper.isValidPath(path)) {
      return ResponseJsonHelper.getPreviewInfoResult("参数错误!");
    }
    String extension = FilenameUtils.getExtension(path).toLowerCase();
    if (allowPreviewExtension.indexOf(extension) == -1) {
      return ResponseJsonHelper.getPreviewInfoResult("该文件不支持预览!");
    }
    String defaultErrMsg = "该文件不可以预览!";
    String docType = DocTypeHelper.getDocType(path).getName();
    counterService.inc("docType." + docType);
    PreviewInfoEx previewInfoEx = previewInfoHelper.getPreviewInfo(employeeInfo, path, securityGroup, width);
    log.info("previewInfo:{}", previewInfoEx);
    if (previewInfoEx.isSuccess()) {
      PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
      if (previewInfo == null || previewInfo.getPageCount() == 0) {
        return ResponseJsonHelper.getPreviewInfoResult(defaultErrMsg);
      } else {
        return ResponseJsonHelper.getPreviewInfoResult(previewInfo.getPageCount(), previewInfo.getSheetNames(), path, securityGroup,previewInfo.getPdfConvertType());
      }
    } else {
      String errMsg = Strings.isNullOrEmpty(previewInfoEx.getErrorMsg()) ? defaultErrMsg : previewInfoEx.getErrorMsg();
      return ResponseJsonHelper.getPreviewInfoResult(errMsg);
    }
  }

  @RequestMapping(value = "/preview/getFilePath")
  public void getFilePath(HttpServletRequest request, HttpServletResponse response) {
    String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
    String page = UrlParametersHelper.safeGetRequestParameter(request, "page");
    String securityGroup = UrlParametersHelper.safeGetRequestParameter(request, "sg");
    int width = NumberUtils.toInt(UrlParametersHelper.safeGetRequestParameter(request, "width"), 1000);
    if (!UrlParametersHelper.isValidPath(path)) {
      response.setStatus(400);
      return;
    }
    try {
      int pageIndex = page.isEmpty() ? 0 : Integer.parseInt(page);
      EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
      PreviewInfoEx previewInfoEx = previewInfoHelper.getPreviewInfo(employeeInfo, path, securityGroup, width);
      if (!previewInfoEx.isSuccess()) {
        log.warn("can't resolve path:{},page:{},reason:can't get preview info", path, page);
        response.setStatus(404);
      } else {
        PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
        if (previewInfo == null) {
          log.warn("can't resolve path:{},page:{},reason:can't get preview info", path, page);
          response.setStatus(404);
        } else {
          if (pageIndex >= previewInfo.getPageCount()) {
            log.warn("invalid page,path:{},page:{}", path, page);
            response.setStatus(400);
          } else {
            String filePath = previewInfo.getOriginalFilePath();
            String dataFilePath = previewInfoDao.getDataFilePath(path, pageIndex, previewInfo.getDataDir(), filePath, 1, previewInfo
              .getFilePathList());
            if (!Strings.isNullOrEmpty(dataFilePath)) {
              fileOutPutor.outPut(response, dataFilePath, false);
            } else {
              String originalFilePath = previewInfo.getOriginalFilePath();
              boolean flag = officeApiHelper.convertExcel2Html(originalFilePath, pageIndex);
              if (flag) {
                dataFilePath = FilenameUtils.getFullPathNoEndSeparator(originalFilePath) + "/" + page + ".html";
                HandlerHtml.process(dataFilePath, pageIndex);
                log.info("dataFilePath:{}", dataFilePath);
                previewInfoDao.savePreviewInfo(employeeInfo.getEa(), path, dataFilePath, width);
                fileOutPutor.outPut(response, dataFilePath, false);
              } else {
                log.warn("can't resolve path:{},page:{}", path, page);
                response.setStatus(404);
              }
            }
          }
        }
      }
    } catch (Exception e) {
      log.error("can't resolve path:{},page:{},reason:happened exception!", path, page, e);
      response.setStatus(404);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/preview/getSheetNames", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public String getSheetNames(HttpServletRequest request) throws Exception {
    String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
    String securityGroup = UrlParametersHelper.safeGetRequestParameter(request, "sg");
    EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
    Map<String, Object> map = new HashMap<>();
    if (path.equals("")) {
      map.put("success", false);
      map.put("errorMsg", "参数错误!");
    } else {
      PreviewInfoEx previewInfoEx = previewInfoHelper.getPreviewInfo(employeeInfo, path, securityGroup, 1000);
      log.info("previewInfoEx:{}", previewInfoEx);
      if (previewInfoEx != null && previewInfoEx.getPreviewInfo() != null) {
        map.put("success", true);
        map.put("sheets", previewInfoEx.getPreviewInfo().getSheetNames());
      } else {
        map.put("success", false);
        map.put("errorMsg", "系统错误!");
      }
    }
    return JSONObject.toJSONString(map);
  }


  @ResponseBody
  @RequestMapping(value = "/preview/getOriginalPreviewInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public String getOriginalPreviewInfo(HttpServletRequest request) {
    String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
    String securityGroup = UrlParametersHelper.safeGetRequestParameter(request, "sg");
    EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
    Map<String, Object> map = new HashMap<>();
    if (path.equals("")) {
      map.put("success", false);
      map.put("errorMsg", "参数错误!");
    } else {
      PreviewInfoEx previewInfoEx = previewInfoHelper.getPreviewInfo(employeeInfo, path, securityGroup, 1000);
      if (previewInfoEx != null && previewInfoEx.getPreviewInfo() != null) {
        map.put("success", true);
        map.put("dirName", previewInfoEx.getPreviewInfo().getDirName());
        map.put("fileName", FilenameUtils.getName(previewInfoEx.getPreviewInfo().getOriginalFilePath()));
      } else {
        map.put("success", false);
        map.put("errorMsg", "系统错误!");
      }
    }
    return JSONObject.toJSONString(map);
  }


  @ResponseBody
  @RequestMapping(value = "/preview/DocPreviewByPath", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public String docPreviewByPath(HttpServletRequest request) throws Exception {
    int pageCount = 0;
    EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
    String path = UrlParametersHelper.safeGetRequestParameter(request, "npath") == "" ?
      UrlParametersHelper.safeGetRequestParameter(request, "path") :
      UrlParametersHelper.safeGetRequestParameter(request, "path");
    if (!UrlParametersHelper.isValidPath(path)) {
      return ResponseJsonHelper.getDocPreviewInfoResult(path, pageCount);
    }
    String extension = FilenameUtils.getExtension(path).toLowerCase();
    if (allowPreviewExtension.indexOf(extension) == -1) {
      return ResponseJsonHelper.getDocPreviewInfoResult(path, pageCount);
    }
    PreviewInfoEx previewInfoEx = previewInfoHelper.getPreviewInfo(employeeInfo, path, "", 1000);
    if (previewInfoEx.isSuccess()) {
      PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
      if (previewInfo != null) {
        pageCount = previewInfo.getPageCount();
      }
    }
    return ResponseJsonHelper.getDocPreviewInfoResult(path, pageCount);
  }

  @ResponseBody
  @RequestMapping(value = "/preview/DocPageByPath", method = RequestMethod.GET)
  public void docPageByPath(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String path = UrlParametersHelper.safeGetRequestParameter(request, "npath") == "" ?
      UrlParametersHelper.safeGetRequestParameter(request, "path") :
      UrlParametersHelper.safeGetRequestParameter(request, "npath");
    int pageIndex = NumberUtils.toInt(UrlParametersHelper.safeGetRequestParameter(request, "pageIndex"), 0);
    if (!UrlParametersHelper.isValidPath(path)) {
      response.setStatus(400);
      return;
    }
    int width = NumberUtils.toInt(UrlParametersHelper.safeGetRequestParameter(request, "width"), 1024);
    width = width > 1920 ? 1920 : width;
    EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
    DocPageResult result = previewService.getDocPage(employeeInfo, path, pageIndex);
    if (result.getCode() == 200) {
      fileOutPutor.outPut(response, result.getDataFilePath(), width, true);
    }
  }


  @ResponseBody
  @RequestMapping(value = "/preview/checkPdf2HtmlStatus", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public void checkPdf2HtmlStatus(HttpServletRequest request, HttpServletResponse response) {
    String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
    if (!UrlParametersHelper.isValidPath(path)) {
      response.setStatus(400);
    }
    try {
      String widthStr = UrlParametersHelper.safeGetRequestParameter(request, "width");
      int width = 1000;
      if (htmlWidthList.contains(widthStr)) {
        width = NumberUtils.toInt(widthStr);
      }
      EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
      String ea = employeeInfo.getEa();
      convertOffice2PdfEnqueueUtil.enqueue(ea, path, width);
    } catch (Exception e) {
      log.warn("checkPdf2HtmlStatus happened exception", e);
    } finally {
      response.setStatus(200);
    }
  }


  @ResponseBody
  @RequestMapping(value = "/preview/queryPdf2HtmlStatus", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public String queryPdf2HtmlStatus(HttpServletRequest request, HttpServletResponse response) {
    String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
    String securityGroup = UrlParametersHelper.safeGetRequestParameter(request, "sg");
    int width = NumberUtils.toInt(UrlParametersHelper.safeGetRequestParameter(request, "width"), 1000);
    if (!UrlParametersHelper.isValidPath(path)) {
      response.setStatus(400);
      return "";
    }
    try {
      EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
      PreviewInfoEx previewInfoEx = previewInfoHelper.getPreviewInfo(employeeInfo, path, securityGroup, width);
      if (!previewInfoEx.isSuccess()) {
        return "";
      } else {
        PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
        if (previewInfo == null) {
          return "";
        } else {
          List<String> dataFilePathList = previewInfo.getFilePathList();
          if (dataFilePathList == null) {
            dataFilePathList = Lists.newArrayList();
          } else {
            String queryExtension = previewInfo.getPdfConvertType() == 0 ? ".html" : ".png";
            dataFilePathList = dataFilePathList.stream().filter(f -> f.endsWith(queryExtension)).
              sorted(Comparator.comparingInt(o -> NumberUtils.toInt(FilenameUtils.getBaseName(o)))).
                                                 collect(Collectors.toList());
          }
          //转换完毕后清理原始文件
          List<Path> pathList;
          if (dataFilePathList.size() == previewInfo.getPageCount()) {
            try (Stream<Path> stream = Files.list(Paths.get(previewInfo.getDataDir())).filter(p -> {
              String fileName = p.toFile().getName();
              String ext = FilenameUtils.getExtension(fileName).toLowerCase();
              return ext.contains("ppt") || ext.contains("doc") || ext.contains("pdf");
            })) {
              pathList = stream.collect(Collectors.toList());
            }
            if (pathList != null) {
              pathList.forEach(path1 -> FileUtils.deleteQuietly(path1.toFile()));
            }
          }
          Map<String, Object> map = new HashMap<>();
          map.put("list", dataFilePathList);
          return JSONObject.toJSONString(map);
        }
      }
    } catch (Exception e) {
      return "";
    }
  }

  @ResponseBody
  @RequestMapping(value = "/share/preview/parseShareToken", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public String parseShareToken(HttpServletRequest request) {
    String shareToken = UrlParametersHelper.safeGetRequestParameter(request, "shareToken");
    Map<String, Object> map = new HashMap<>();
    if (Strings.isNullOrEmpty(shareToken)) {
      map.put("success", false);
      map.put("errorMsg", "参数错误!");
    } else {
      ShareTokenParamInfo shareTokenParamInfo = ShareTokenUtil.convertToken2ParamInfo(shareToken);
      if (shareTokenParamInfo != null) {
        map.put("success", true);
        map.put("data", shareTokenParamInfo);
      } else {
        map.put("success", false);
        map.put("errorMsg", "参数错误!");
      }
    }
    return JSONObject.toJSONString(map);
  }
}

