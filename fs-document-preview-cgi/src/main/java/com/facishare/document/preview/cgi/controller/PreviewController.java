package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.model.result.ConvertDocResult;
import com.facishare.document.preview.api.service.DocConvertService;
import com.facishare.document.preview.cgi.dao.FileTokenDao;
import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.*;
import com.facishare.document.preview.cgi.service.PreviewService;
import com.facishare.document.preview.cgi.utils.DocPageInfoHelper;
import com.facishare.document.preview.cgi.utils.FileOutPutor;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.cgi.utils.RequestParamsHelper;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import com.fxiaoke.metrics.CounterService;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


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
    DocConvertService docConvertService;
    @Autowired
    private PreviewService previewService;
    @Autowired
    private CounterService counterService;
    @Autowired
    private FileOutPutor fileOutPutor;
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
        if (!isValidPath(path)) {
            response.setStatus(400);
            return;
        }
        String page = safteGetRequestParameter(request, "page");
        String securityGroup = safteGetRequestParameter(request, "sg");
        int pageIndex = page.isEmpty() ? 0 : Integer.parseInt(page);
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String ea = employeeInfo.getEa();
        PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, securityGroup);
        if (previewInfoEx.isSuccess()) {
            PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
            if (previewInfo != null) {
                DataFileInfo dataFileInfo = previewInfoDao.getDataFileInfo(ea, path, pageIndex, previewInfo);
                if (!dataFileInfo.getShortFilePath().equals("")) {
                    fileOutPutor.outPut(response, dataFileInfo.getShortFilePath());
                } else {
                    String originalFilePath = dataFileInfo.getOriginalFilePath();
                    ConvertDocArg convertDocArg = ConvertDocArg.builder().originalFilePath(originalFilePath).page(pageIndex).path(path).build();
                    ConvertDocResult convertDocResult = docConvertService.convertDoc(convertDocArg);
                    String dataFilePath = convertDocResult.getDataFilePath();
                    if (!Strings.isNullOrEmpty(dataFilePath)) {
                        fileOutPutor.outPut(response, dataFilePath);
                    } else {
                        log.warn("can't resolve path:{},page:{}", path, page);
                        response.setStatus(404);
                    }
                }
            } else {
                log.warn("can't resolve path:{},page:{}", path, page);
                response.setStatus(404);
            }
        } else
            log.warn("can't resolve path:{},page:{}", path, page);
        response.setStatus(404);
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
    @RequestMapping(value = "/preview/DocPreviewByPath", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String docPreviewByPath(HttpServletRequest request) throws Exception {
        int pageCount = 0;
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String path = RequestParamsHelper.safteGetRequestParameter(request, "npath") == "" ? RequestParamsHelper.safteGetRequestParameter(request, "path") : RequestParamsHelper.safteGetRequestParameter(request, "path");
        if (!isValidPath(path)) {
            return getDocPreviewInfoResult(path,0);
        }
        String extension = FilenameUtils.getExtension(path).toLowerCase();
        if (allowPreviewExtension.indexOf(extension) == -1) {
            return getDocPreviewInfoResult(path,0);
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
    public void docPageByPath(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String path = RequestParamsHelper.safteGetRequestParameter(request, "npath") == "" ? RequestParamsHelper.safteGetRequestParameter(request, "path") : RequestParamsHelper.safteGetRequestParameter(request, "npath");
        if (!isValidPath(path)) {
            response.setStatus(400);
            return;
        }
        int pageIndex = NumberUtils.toInt(safteGetRequestParameter(request, "pageIndex"), 0);
        int width = NumberUtils.toInt(safteGetRequestParameter(request, "width"), 1024);
        width = width > 1920 ? 1920 : width;
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String ea = employeeInfo.getEa();
        PreviewInfoEx previewInfoEx = previewService.getPreviewInfo(employeeInfo, path, "");
        if (previewInfoEx.isSuccess()) {
            PreviewInfo previewInfo = previewInfoEx.getPreviewInfo();
            if (previewInfo != null) {
                DataFileInfo dataFileInfo = previewInfoDao.getDataFileInfo(ea, path, pageIndex, previewInfo);
                if (!dataFileInfo.getShortFilePath().equals("")) {
                    fileOutPutor.outPut(response, dataFileInfo.getShortFilePath(), width);
                } else {
                    String originalFilePath = dataFileInfo.getOriginalFilePath();
                    ConvertDocArg convertDocArg = ConvertDocArg.builder().originalFilePath(originalFilePath).page(pageIndex).path(path).build();
                    ConvertDocResult convertDocResult = docConvertService.convertDoc(convertDocArg);
                    String dataFilePath = convertDocResult.getDataFilePath();
                    if (!Strings.isNullOrEmpty(dataFilePath)) {
                        previewInfoDao.savePreviewInfo(ea, path, dataFilePath);
                        fileOutPutor.outPut(response, dataFileInfo.getShortFilePath(), width);
                    } else {
                        log.warn("can't resolve path:{},page:{}", path, pageIndex);
                        response.setStatus(404);
                    }
                }
            } else {
                log.warn("can't resolve path:{},page:{}", path, pageIndex);
                response.setStatus(404);
            }
        } else {
            log.warn("can't get previewInfo,path:{},pageIndex:{}", path, pageIndex);
            response.setStatus(404);
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

    private String getDocPreviewInfoResult(String path, int pageCount) throws Exception {
        DocPageInfo docPageInfo = DocPageInfoHelper.getDocPageInfo(path);
        docPageInfo.setPageCount(pageCount);
        return JSONObject.toJSONString(docPageInfo);
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

