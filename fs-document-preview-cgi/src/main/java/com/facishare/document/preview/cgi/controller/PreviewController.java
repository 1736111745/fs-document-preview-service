package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.arg.Pdf2HtmlArg;
import com.facishare.document.preview.api.model.result.ConvertDocResult;
import com.facishare.document.preview.api.model.result.Pdf2HtmlResult;
import com.facishare.document.preview.api.service.DocConvertService;
import com.facishare.document.preview.api.service.Pdf2HtmlService;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.service.PreviewService;
import com.facishare.document.preview.cgi.utils.*;
import com.facishare.document.preview.common.dao.FileTokenDao;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.*;
import com.facishare.document.preview.common.utils.*;
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
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by liuq on 16/8/5.
 */
@Slf4j
@Controller
@RequestMapping("/")
public class PreviewController {

    @Autowired
    FileStorageProxy fileStorageProxy;
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    FileTokenDao fileTokenDao;
    @Autowired
    PreviewService previewService;
    @Autowired
    CounterService counterService;
    @Autowired
    ConvertOffice2PdfEnqueueUtil convertOffice2PdfEnqueueUtil;
    @Autowired
    OfficeApiHelper officeApiHelper;
    @ReloadableProperty("allowPreviewExtension")
    private String allowPreviewExtension = "doc|docx|xls|xlsx|ppt|pptx|pdf";

    @ResponseBody
    @RequestMapping(value = "/preview/getPreviewInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getPreviewInfo(HttpServletRequest request) throws Exception {
        String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
        String token = UrlParametersHelper.safeGetRequestParameter(request, "token");
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String securityGroup = "";
        if (path.equals("") && token.equals("")) {
            return getPreviewInfoResult("参数错误!");
        }
        if (!token.equals("")) {
            log.info("getTokenInfo,ea:{},token:{},sourceUser:{}", employeeInfo.getEa(), token, employeeInfo.getSourceUser());
            DownloadFileTokens fileToken = fileTokenDao.getInfo(employeeInfo.getEa(), token, employeeInfo.getSourceUser());
            if (fileToken != null && fileToken.getFileType().toLowerCase().equals("preview")) {
                {
                    path = Strings.isNullOrEmpty(fileToken.getFilePath()) ? "" : fileToken.getFilePath().trim();
                    securityGroup = Strings.isNullOrEmpty(fileToken.getDownloadSecurityGroup()) ? "" : fileToken.getDownloadSecurityGroup().trim();
                }
            }
        }
        if (!UrlParametersHelper.isValidPath(path)) {
            return getPreviewInfoResult("参数错误!");
        }
        String extension = FilenameUtils.getExtension(path).toLowerCase();
        if (allowPreviewExtension.indexOf(extension) == -1) {
            return getPreviewInfoResult("该文件不支持预览!");
        }
        String defaultErrMsg = "该文件不可以预览!";
        String docType = DocTypeHelper.getDocType(path).getName();
        counterService.inc("docType." + docType);
        PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, securityGroup);
        if (previewInfoEx.isSuccess()) {
            PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
            if (previewInfo == null || previewInfo.getPageCount() == 0) {
                return getPreviewInfoResult(defaultErrMsg);
            } else {
                return getPreviewInfoResult(previewInfo.getPageCount(), previewInfo.getSheetNames(), path, securityGroup);
            }
        } else {
            String errMsg = Strings.isNullOrEmpty(previewInfoEx.getErrorMsg()) ? defaultErrMsg : previewInfoEx.getErrorMsg();
            return getPreviewInfoResult(errMsg);
        }
    }

    @RequestMapping(value = "/preview/getFilePath")
    public void getFilePath(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
        String page = UrlParametersHelper.safeGetRequestParameter(request, "page");
        String securityGroup = UrlParametersHelper.safeGetRequestParameter(request, "sg");
        if (!UrlParametersHelper.isValidPath(path)) {
            response.setStatus(400);
            return;
        }
        try {
            int pageIndex = page.isEmpty() ? 0 : Integer.parseInt(page);
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, securityGroup);
            if (previewInfoEx.isSuccess()) {
                PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
                if (previewInfo != null) {
                    if (pageIndex < previewInfo.getPageCount()) {
                        String filePath = previewInfo.getOriginalFilePath();
                        String dataFilePath = previewInfoDao.getDataFilePath(path, pageIndex, previewInfo.getDataDir(), filePath, 1, previewInfo.getFilePathList());
                        if (!Strings.isNullOrEmpty(dataFilePath)) {
                            FileOutPutor.outPut(response, dataFilePath, true);
                        } else {
                            String originalFilePath = previewInfo.getOriginalFilePath();
                            boolean flag = officeApiHelper.convertExcel2Html(path, originalFilePath, pageIndex);
                            if (flag) {
                                dataFilePath = FilenameUtils.getFullPathNoEndSeparator(originalFilePath) + "/" + page + ".html";
                                HandlerHtml.process(dataFilePath,pageIndex);
                                log.info("dataFilePath:{}",dataFilePath);
                                previewInfoDao.savePreviewInfo(employeeInfo.getEa(), path, dataFilePath);
                                FileOutPutor.outPut(response, dataFilePath, true);
                            } else {
                                log.warn("can't resolve path:{},page:{}", path, page);
                                response.setStatus(404);
                            }
                        }
                    } else {
                        log.warn("invalid page,path:{},page:{}", path, page);
                        response.setStatus(400);
                    }
                } else {
                    log.warn("can't resolve path:{},page:{},reason:can't get preview info", path, page);
                    response.setStatus(404);
                }
            } else {
                log.warn("can't resolve path:{},page:{},reason:can't get preview info", path, page);
                response.setStatus(404);
            }
        } catch (Exception e) {
            log.error("can't resolve path:{},page:{},reason:happened exception!", path, page, e);
            response.setStatus(404);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/preview/getSheetNames", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getSheetNames(HttpServletRequest request) {
        String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String ea = employeeInfo.getEa();
        Map<String, Object> map = new HashMap<>();
        if (path.equals("")) {
            map.put("success", false);
            map.put("errorMsg", "参数错误!");
        } else {
            PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
            if (previewInfo != null) {
                map.put("success", true);
                map.put("sheets", previewInfo.getSheetNames());
            } else {
                map.put("success", false);
                map.put("errorMsg", "系统错误!");
            }
        }
        return JSONObject.toJSONString(map);
    }


    @ResponseBody
    @RequestMapping(value = "/preview/getDirName", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getDirName(HttpServletRequest request) {
        String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String ea = employeeInfo.getEa();
        Map<String, Object> map = new HashMap<>();
        if (path.equals("")) {
            map.put("success", false);
            map.put("errorMsg", "参数错误!");
        } else {
            PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
            if (previewInfo != null) {
                map.put("success", true);
                map.put("dirName", previewInfo.getDirName());
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
        String path = RequestParamsHelper.safeGetRequestParameter(request, "npath") == "" ? RequestParamsHelper.safeGetRequestParameter(request, "path") : RequestParamsHelper.safeGetRequestParameter(request, "path");
        if (!UrlParametersHelper.isValidPath(path)) {
            return getDocPreviewInfoResult(path, pageCount);
        }
        String extension = FilenameUtils.getExtension(path).toLowerCase();
        if (allowPreviewExtension.indexOf(extension) == -1) {
            return getDocPreviewInfoResult(path, pageCount);
        }
        PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, "");
        if (previewInfoEx.isSuccess()) {
            PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
            if (previewInfo != null) {
                pageCount = previewInfo.getPageCount();
            }
        }
        return getDocPreviewInfoResult(path, pageCount);
    }

    @ResponseBody
    @RequestMapping(value = "/preview/DocPageByPath", method = RequestMethod.GET)
    public void docPageByPath(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = RequestParamsHelper.safeGetRequestParameter(request, "npath") == "" ? RequestParamsHelper.safeGetRequestParameter(request, "path") : RequestParamsHelper.safeGetRequestParameter(request, "npath");
        int pageIndex = NumberUtils.toInt(UrlParametersHelper.safeGetRequestParameter(request, "pageIndex"), 0);
        if (!UrlParametersHelper.isValidPath(path)) {
            response.setStatus(400);
            return;
        }
        try {
            int width = NumberUtils.toInt(UrlParametersHelper.safeGetRequestParameter(request, "width"), 1024);
            width = width > 1920 ? 1920 : width;
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, "");
            if (previewInfoEx.isSuccess()) {
                PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
                if (previewInfo != null) {
                    if (pageIndex < previewInfo.getPageCount()) {
                        String dataFilePath = previewInfoDao.getDataFilePath(path, pageIndex, previewInfo.getDataDir(), previewInfo.getOriginalFilePath(), 2, previewInfo.getFilePathList());
                        if (!Strings.isNullOrEmpty(dataFilePath)) {
                            FileOutPutor.outPut(response, dataFilePath, width, true);
                        } else {
                            String originalFilePath = previewInfo.getOriginalFilePath();
                            boolean result = officeApiHelper.convertOffice2Png(path, originalFilePath, pageIndex);
                            if (result) {
                                dataFilePath = FilenameUtils.getFullPathNoEndSeparator(originalFilePath) + "/" + (pageIndex + 1) + ".png";
                                log.info("dataFilePath:{}", dataFilePath);
                                previewInfoDao.savePreviewInfo(employeeInfo.getEa(), path, dataFilePath);
                                FileOutPutor.outPut(response, dataFilePath, true);
                            } else {
                                log.warn("can't resolve path:{},page:{}", path, pageIndex);
                                response.setStatus(404);
                            }
                        }
                    } else {
                        log.warn("invalid page,path:{},page:{}", path, pageIndex);
                        response.setStatus(400);
                    }
                } else {
                    log.warn("can't resolve path:{},page:{}", path, pageIndex);
                    response.setStatus(404);
                }
            } else {
                log.warn("can't get previewInfo,path:{},pageIndex:{}", path, pageIndex);
                response.setStatus(404);
            }
        } catch (Exception e) {
            log.warn("can't get previewInfo,path:{},pageIndex:{}", path, pageIndex, e);
            response.setStatus(404);
        }
    }


    @ResponseBody
    @RequestMapping(value = "/preview/checkPdf2HtmlStatus", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void checkPdf2HtmlStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
        if (!UrlParametersHelper.isValidPath(path)) {
            response.setStatus(400);
        }
        try {
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            String ea = employeeInfo.getEa();
            convertOffice2PdfEnqueueUtil.enqueue(ea, path);
        } catch (Exception e) {
            log.warn("checkPdf2HtmlStatus happened exception", e);
        } finally {
            response.setStatus(200);
        }
    }


    @ResponseBody
    @RequestMapping(value = "/preview/queryPdf2HtmlStatus", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String queryPdf2HtmlStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
        String securityGroup = UrlParametersHelper.safeGetRequestParameter(request, "sg");
        if (!UrlParametersHelper.isValidPath(path)) {
            response.setStatus(400);
            return "";
        }
        try {
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, securityGroup);
            if (!previewInfoEx.isSuccess()) {
                return "";
            } else {
                PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
                if (previewInfo == null) {
                    return "";
                } else {
                    List<String> dataFilePathList = previewInfo.getFilePathList();
                    if (dataFilePathList == null)
                        dataFilePathList = Lists.newArrayList();
                    else
                        dataFilePathList = dataFilePathList.stream().filter(f -> f.endsWith(".html")).
                                sorted(Comparator.comparingInt(o -> NumberUtils.toInt(FilenameUtils.getBaseName(o)))).
                                collect(Collectors.toList());
                    //转换完毕后清理原始文件
                    if (dataFilePathList.size() == previewInfo.getPageCount()) {
                        Files.list(Paths.get(previewInfo.getDataDir())).filter(p ->
                        {
                            String fileName = p.toFile().getName();
                            String ext = FilenameUtils.getExtension(fileName).toLowerCase();
                            return ext.contains("ppt") || ext.contains("doc") || ext.contains("pdf");
                        }).forEach(p1 -> {
                            FileUtils.deleteQuietly(p1.toFile());
                        });
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


    private String getPreviewInfoResult(int pageCount, List<String> sheetNames, String path, String securityGroup) {
        Map<String, Object> map = new HashMap<>();
        map.put("canPreview", true);
        map.put("pageCount", pageCount);
        map.put("path", path);
        map.put("sg", securityGroup);
        map.put("sheets", sheetNames);
        return JSONObject.toJSONString(map);
    }

    private String getPreviewInfoResult(String errorMsg) {
        Map<String, Object> map = new HashMap<>();
        map.put("canPreview", false);
        map.put("errorMsg", errorMsg);
        return JSONObject.toJSONString(map);
    }

    private String getDocPreviewInfoResult(String path, int pageCount) throws Exception {
        PreviewJsonInfo docPreviewInfo = DocPreviewInfoHelper.getPreviewInfo(path);
        docPreviewInfo.setPageCount(pageCount);
        return JSONObject.toJSONString(docPreviewInfo);
    }


}

