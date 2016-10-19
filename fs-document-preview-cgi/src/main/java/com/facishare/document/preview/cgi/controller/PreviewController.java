package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.cgi.convertor.DocConvertor;
import com.facishare.document.preview.cgi.dao.FileTokenDao;
import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.*;
import com.facishare.document.preview.cgi.utils.DocPageInfoHelper;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.cgi.utils.PathHelper;
import com.facishare.document.preview.cgi.utils.SampleUUID;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


/**
 * Created by liuq on 16/8/5.
 */
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
    DocConvertor docConvertor;
    private static final Logger LOG = LoggerFactory.getLogger(PreviewController.class);
    @ReloadableProperty("allowPreviewExtension")
    private String allowPreviewExtension = "doc|docx|xls|xlsx|ppt|pptx|pdf";

    @ResponseBody
    @RequestMapping(value = "/preview/getPreviewInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public WebAsyncTask<String> getPreviewInfo(HttpServletRequest request) throws Exception {
        Callable<String> callable = () ->
        {
            String path = safteGetRequestParameter(request, "path");
            String token = safteGetRequestParameter(request, "token");
            String securityGroup = "";
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            if (path.equals("") && token.equals("")) {
                return getPreviewInfoResult(false, 0, null, "", "参数错误!");
            }
            if (!token.equals("")) {
                DownloadFileTokens fileToken = fileTokenDao.getInfo(employeeInfo.getEa(), token, employeeInfo.getSourceUser());
                if (fileToken != null && fileToken.getFileType().toLowerCase().equals("preview")) {
                    {
                        path = fileToken.getFilePath() == null ? "" : fileToken.getFilePath().trim();
                        securityGroup = fileToken.getDownloadSecurityGroup();
                    }
                }
            }
            if (path.isEmpty()) {
                return getPreviewInfoResult(false, 0, null, "", "参数错误!");
            }
            String extension = FilenameUtils.getExtension(path).toLowerCase();
            if (allowPreviewExtension.indexOf(extension) == -1) {
                return getPreviewInfoResult(false, 0, null, "", "该文件不可以预览!");
            }
            PreviewInfo previewInfo = previewInfoDao.getInfoByPath(path);
            int pageCount;
            List<String> sheetNames;
            if (previewInfo == null) {
                try {
                    byte[] bytes = fileStorageProxy.GetBytesByPath(path, employeeInfo, securityGroup);
                    if (bytes == null || bytes.length == 0) {
                        return getPreviewInfoResult(false, 0, null, "", "文件无法找到或者损坏!");
                    }
                    String dataDir = new PathHelper(employeeInfo.getEa()).getDataDir();
                    String fileName = SampleUUID.getUUID() + "." + extension;
                    String filePath = FilenameUtils.concat(dataDir, fileName);
                    FileUtils.writeByteArrayToFile(new File(filePath), bytes);
                    PageInfo pageInfo = DocPageInfoHelper.GetDocPageCount(bytes, filePath);
                    pageCount = pageInfo.getPageCount();
                    sheetNames = pageInfo.getSheetNames();
                    previewInfoDao.initPreviewInfo(path, filePath, dataDir, bytes.length, pageCount, sheetNames, employeeInfo.getEa(), employeeInfo.getEmployeeId());
                    return getPreviewInfoResult(true, pageCount, pageInfo.getSheetNames(), path, "");
                } catch (Exception ex) {
                    return getPreviewInfoResult(false, 0, null, "", "该文件不可以预览!");
                }
            }
            pageCount = previewInfo.getPageCount();
            sheetNames = previewInfo.getSheetNames();
            if (pageCount == 0) {
                return getPreviewInfoResult(false, 0, null, "", "该文件不可以预览!");
            }
            return getPreviewInfoResult(true, pageCount, sheetNames, path, "");
        };
        return new WebAsyncTask<>(1000 * 60, callable);
    }


    @RequestMapping(value = "/preview/getFilePath")
    public WebAsyncTask convert(HttpServletRequest request) throws Exception {
        Callable<ModelAndView> callable = () -> {
            String path = safteGetRequestParameter(request, "path");
            String page = safteGetRequestParameter(request, "page");
            String name = safteGetRequestParameter(request, "name");
            String pageCount = safteGetRequestParameter(request, "pageCount");
            int pageCnt = pageCount.isEmpty() ? 0 : Integer.parseInt(pageCount);
            int pageIndex = page.isEmpty() ? 0 : Integer.parseInt(page);
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            DataFileInfo dataFileInfo = previewInfoDao.getDataFileInfo(path, pageIndex, employeeInfo.getEa());
            if (!dataFileInfo.getShortFilePath().equals("")) {
                return handModelAndView(dataFileInfo.getShortFilePath());

            } else {
                String originalFilePath = dataFileInfo.getOriginalFilePath();
                File file = new File(originalFilePath);
                String dataFilePath = docConvertor.doConvert(path, dataFileInfo.getDataDir(), name, originalFilePath, pageIndex);
                previewInfoDao.create(path, dataFileInfo.getDataDir(), dataFilePath, employeeInfo.getEa(), employeeInfo.getEmployeeId(), file.length(), pageCnt);
                return handModelAndView(dataFilePath);
            }
        };
        return new WebAsyncTask(1000 * 60, callable);
    }


    @ResponseBody
    @RequestMapping(value = "/preview/getSheetNames", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getSheetNames(HttpServletRequest request) {
        String path = safteGetRequestParameter(request, "path");
        Map<String, Object> map = new HashMap<>();
        if (path.equals("")) {
            map.put("success", false);
            map.put("errorMsg", "参数错误!");
        } else {
            PreviewInfo previewInfo = previewInfoDao.getInfoByPath(path);
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

    private ModelAndView handModelAndView(String dataFilePath) {
        if (dataFilePath.length() > 0) {
            String[] array = dataFilePath.split("/");
            if (array.length == 2) {
                return new ModelAndView("redirect:/preview/" + array[0] + "/" + array[1]);
            } else
                return new ModelAndView("redirect:/preview/static/images/pixel.gif");
        } else {
            return new ModelAndView("redirect:/preview/static/images/pixel.gif");
        }
    }




    private String safteGetRequestParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName) == null ? "" : request.getParameter(paramName).trim();
        return value;
    }

    private String getPreviewInfoResult(boolean canPreview, int pageCount, List<String> sheetNames,String path,  String errorMsg) {
        Map<String, Object> map = new HashMap<>();
        map.put("canPreview", canPreview);
        if (canPreview) {
            map.put("pageCount", pageCount);
            map.put("path", path);
            map.put("sheets",sheetNames);
        } else
            map.put("errorMsg", errorMsg);
        return JSONObject.toJSONString(map);
    }

    @ExceptionHandler
    @ResponseBody
    public void handleException(HttpServletResponse req, Exception e) {
        LOG.error("error:", e);
        req.setStatus(500);
    }
}

