package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.api.model.arg.AsyncConvertDocArg;
import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.arg.Pdf2HtmlArg;
import com.facishare.document.preview.api.model.result.ConvertDocResult;
import com.facishare.document.preview.api.model.result.Pdf2HtmlResult;
import com.facishare.document.preview.api.service.DocConvertService;
import com.facishare.document.preview.api.service.Pdf2HtmlService;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.service.PreviewService;
import com.facishare.document.preview.cgi.utils.FileOutPutor;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.cgi.utils.PdfHelper;
import com.facishare.document.preview.cgi.utils.RequestParamsHelper;
import com.facishare.document.preview.common.dao.ConvertTaskDao;
import com.facishare.document.preview.common.dao.FileTokenDao;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.*;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
import com.facishare.document.preview.common.utils.DocPreviewInfoHelper;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import com.fxiaoke.metrics.CounterService;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    ConvertTaskDao convertTaskDao;
    @Autowired
    FileTokenDao fileTokenDao;
    @Autowired
    DocConvertService docConvertService;
    @Autowired
    Pdf2HtmlService pdf2HtmlService;
    @Autowired
    private PreviewService previewService;
    @Autowired
    private CounterService counterService;
    @Autowired
    private ConvertorQueueProvider convertorQueueProvider;
    @ReloadableProperty("allowPreviewExtension")
    private String allowPreviewExtension = "doc|docx|xls|xlsx|ppt|pptx|pdf";

    @ResponseBody
    @RequestMapping(value = "/preview/getPreviewInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getPreviewInfo(HttpServletRequest request) throws Exception {
        String path = safteGetRequestParameter(request, "path");
        String token = safteGetRequestParameter(request, "token");
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String securityGroup = "";
        if (path.equals("") && token.equals("")) {
            return getPreviewInfoResult(false, 0, null, "", "", "参数错误!");
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
        if (!isValidPath(path)) {
            return getPreviewInfoResult(false, 0, null, "", "", "参数错误!");
        }
        String extension = FilenameUtils.getExtension(path).toLowerCase();
        if (allowPreviewExtension.indexOf(extension) == -1) {
            return getPreviewInfoResult(false, 0, null, "", "", "该文件不支持预览!");
        }
        String defaultErrMsg = "该文件不可以预览!";
        String docType = DocTypeHelper.getDocType(path).getName();
        counterService.inc("docType." + docType);
        PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, securityGroup);
        if (previewInfoEx.isSuccess()) {
            PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
            if (previewInfo == null || previewInfo.getPageCount() == 0) {
                return getPreviewInfoResult(false, 0, null, "", "", defaultErrMsg);
            } else
                return getPreviewInfoResult(true, previewInfo.getPageCount(), previewInfo.getSheetNames(), path, securityGroup, "");
        } else {
            String errMsg = Strings.isNullOrEmpty(previewInfoEx.getErrorMsg()) ? defaultErrMsg : previewInfoEx.getErrorMsg();
            return getPreviewInfoResult(false, 0, null, "", "", errMsg);
        }
    }

    @RequestMapping(value = "/preview/getFilePath")
    public void getFilePath(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = safteGetRequestParameter(request, "path");
        String page = safteGetRequestParameter(request, "page");
        String securityGroup = safteGetRequestParameter(request, "sg");
        String version = safteGetRequestParameter(request, "ver");
        if (!isValidPath(path)) {
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
                        int type = Strings.isNullOrEmpty(version) ? 1 : 2;
                        String dataFilePath = previewInfoDao.getDataFilePath(path, pageIndex, previewInfo.getDataDir(), type, previewInfo.getFilePathList());
                        if (!Strings.isNullOrEmpty(dataFilePath)) {
                            FileOutPutor.outPut(response, dataFilePath, true);
                        } else {
                            String originalFilePath = previewInfo.getOriginalFilePath();
                            DocType docType = DocTypeHelper.getDocType(path);
                            if (docType == DocType.PDF && !Strings.isNullOrEmpty(version)) {
                                Pdf2HtmlArg pdf2HtmlArg = Pdf2HtmlArg.builder().originalFilePath(originalFilePath).page(pageIndex).path(path).build();
                                //log.info("begin do convert,arg:{}", pdf2HtmlArg);
                                Pdf2HtmlResult pdf2HtmlResult = pdf2HtmlService.convertPdf2Html(pdf2HtmlArg);
                                dataFilePath = pdf2HtmlResult.getDataFilePath();
                                //log.info("end do convert,result:{}", pdf2HtmlResult);
                            } else {
                                ConvertDocArg convertDocArg = ConvertDocArg.builder().originalFilePath(originalFilePath).page(pageIndex).path(path).type(1).build();
                                //log.info("begin do convert,arg:{}", convertDocArg);
                                ConvertDocResult convertDocResult = docConvertService.convertDoc(convertDocArg);
                                dataFilePath = convertDocResult.getDataFilePath();
                                //log.info("end do convert,result:{}", convertDocResult);
                            }
                            if (!Strings.isNullOrEmpty(dataFilePath)) {
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
        String path = safteGetRequestParameter(request, "path");
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
        String path = safteGetRequestParameter(request, "path");
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
        String path = RequestParamsHelper.safteGetRequestParameter(request, "npath") == "" ? RequestParamsHelper.safteGetRequestParameter(request, "path") : RequestParamsHelper.safteGetRequestParameter(request, "path");
        if (!isValidPath(path)) {
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
        String path = RequestParamsHelper.safteGetRequestParameter(request, "npath") == "" ? RequestParamsHelper.safteGetRequestParameter(request, "path") : RequestParamsHelper.safteGetRequestParameter(request, "npath");
        int pageIndex = NumberUtils.toInt(safteGetRequestParameter(request, "pageIndex"), 0);
        if (!isValidPath(path)) {
            response.setStatus(400);
            return;
        }
        try {
            int width = NumberUtils.toInt(safteGetRequestParameter(request, "width"), 1024);
            width = width > 1920 ? 1920 : width;
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            String ea = employeeInfo.getEa();
            PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, "");
            if (previewInfoEx.isSuccess()) {
                PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
                if (previewInfo != null) {
                    if (pageIndex < previewInfo.getPageCount()) {
                        String dataFilePath = previewInfoDao.getDataFilePath(path, pageIndex, previewInfo.getDataDir(), 2, previewInfo.getFilePathList());
                        if (!Strings.isNullOrEmpty(dataFilePath)) {
                            FileOutPutor.outPut(response, dataFilePath, width, true);
                        } else {
                            String originalFilePath = previewInfo.getOriginalFilePath();
                            File originalFile = new File(originalFilePath);
                            if (!originalFile.exists()) {
                                fileStorageProxy.DownloadAndSave(path, employeeInfo, "", originalFilePath);
                            }
                            ConvertDocArg convertDocArg = ConvertDocArg.builder().originalFilePath(originalFilePath).page(pageIndex).path(path).type(2).build();
                            ConvertDocResult convertDocResult = docConvertService.convertDoc(convertDocArg);
                            dataFilePath = convertDocResult.getDataFilePath();
                            if (!Strings.isNullOrEmpty(dataFilePath)) {
                                previewInfoDao.savePreviewInfo(ea, path, dataFilePath);
                                FileOutPutor.outPut(response, dataFilePath, width, true);
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
    @RequestMapping(value = "/preview/checkDocConvertStatus", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void checkDocConvertStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = safteGetRequestParameter(request, "path");
        String securityGroup = safteGetRequestParameter(request, "sg");
        if (!isValidPath(path)) {
            response.setStatus(400);
        }
        try {
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            String ea = employeeInfo.getEa();
            PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, securityGroup);
            log.info("previewInfoEx:"+ JSON.toJSONString(previewInfoEx));
            if (previewInfoEx.isSuccess()) {
                PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
                if (previewInfo != null) {
                    int pageCount = previewInfo.getPageCount();
                    List<String> dataFilePathList = previewInfo.getFilePathList();
                    if(dataFilePathList==null)
                        dataFilePathList= Lists.newArrayList();
                    log.info("dataFilePathList:"+ JSON.toJSONString(dataFilePathList));
                    for (int i = 1; i < pageCount + 1; i++) {
                        if (!dataFilePathList.contains(i + ".html")) {
                            int status = convertTaskDao.getTaskStatus(ea, path, i);
                            log.info("convert status:{}",status);
                            if (status == -1) {
                                ConvertorMessage convertorMessage = ConvertorMessage.builder().npath(path).ea(ea).page(i).filePath(previewInfo.getOriginalFilePath()).build();
                                convertTaskDao.addTask(ea, path, i);
                                convertorQueueProvider.convertPdf(convertorMessage);

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        finally {
            response.setStatus(200);
        }
    }


    @ResponseBody
    @RequestMapping(value = "/preview/queryDocConvertStatus", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String queryDocConvertStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String path = safteGetRequestParameter(request, "path");
        String securityGroup = safteGetRequestParameter(request, "sg");
        if (!isValidPath(path)) {
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
                    if(dataFilePathList==null)
                        dataFilePathList= Lists.newArrayList();
                    return getQueryDocConvertStatus(dataFilePathList);
                }
            }
        } catch (Exception e) {
            return "";
        }
    }


    @ResponseBody
    @RequestMapping(value = "/preview/pdf/getData", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void getPdfData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = safteGetRequestParameter(request, "path");
        String page = safteGetRequestParameter(request, "page");
        String securityGroup = safteGetRequestParameter(request, "sg");
        int pageIndex = page.isEmpty() ? 0 : Integer.parseInt(page);
        if (!isValidPath(path)) {
            response.setStatus(400);
        }
        try {
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, securityGroup);
            if (!previewInfoEx.isSuccess()) {
                response.setStatus(400);
            } else {
                PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
                if (previewInfo == null) {
                    response.setStatus(400);
                } else {

                    String partFilePath= PdfHelper.getPdfData(previewInfo.getOriginalFilePath(),pageIndex);
                    FileOutPutor.outPut(response,partFilePath,false);
                }
            }
        } catch (Exception e) {
            response.setStatus(400);
        }
    }


    private String safteGetRequestParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName) == null ? "" : request.getParameter(paramName).trim();
        return value;
    }

    private String getPreviewInfoResult(boolean canPreview, int pageCount, List<String> sheetNames, String path, String securityGroup, String errorMsg) {
        Map<String, Object> map = new HashMap<>();
        map.put("canPreview", canPreview);
        if (canPreview) {
            map.put("pageCount", pageCount);
            map.put("path", path);
            map.put("sg", securityGroup);
            map.put("sheets", sheetNames);
        } else
            map.put("errorMsg", errorMsg);
        return JSONObject.toJSONString(map);
    }


    private String getQueryDocConvertStatus(List<String> filePathList) {
        Map<String, Object> map = new HashMap<>();
        map.put("list", filePathList);
        return JSONObject.toJSONString(map);
    }

    private String getDocPreviewInfoResult(String path, int pageCount) throws Exception {
        PreviewJsonInfo docPreviewInfo = DocPreviewInfoHelper.getPreviewInfo(path);
        docPreviewInfo.setPageCount(pageCount);
        return JSONObject.toJSONString(docPreviewInfo);
    }


    private boolean isValidPath(String path) {
        if (Strings.isNullOrEmpty(path))
            return false;
        if (path.startsWith("N_") || path.startsWith("TN_") || path.startsWith("TG_")
                || path.startsWith("A_") || path.startsWith("TA_")
                || path.startsWith("G_") || path.startsWith("F_") || path.startsWith("S_")) {
            return true;
        }
        String[] pathSplit = path.split("_");
        return pathSplit.length == 3;
    }
}

