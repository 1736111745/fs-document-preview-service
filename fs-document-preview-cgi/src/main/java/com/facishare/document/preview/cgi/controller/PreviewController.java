package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.cgi.convertor.DocConvertor;
import com.facishare.document.preview.cgi.dao.DocPreviewInfoDao;
import com.facishare.document.preview.cgi.dao.FileTokenDao;
import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.*;
import com.facishare.document.preview.cgi.utils.DocPageInfoHelper;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.cgi.utils.PathHelper;
import com.facishare.document.preview.cgi.utils.SampleUUID;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
    DocPreviewInfoDao docPreviewInfoDao;
    @Autowired
    FileTokenDao fileTokenDao;
    @Autowired
    DocConvertor docConvertor;
    private static final Logger logger = LoggerFactory.getLogger(PreviewController.class);
    @ReloadableProperty("allowPreviewExtension")
    private String allowPreviewExtension = "doc|docx|xls|xlsx|ppt|pptx|pdf";

    @ResponseBody
    @RequestMapping(value = "/preview/getPreviewInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public WebAsyncTask<String> getPreviewInfo(HttpServletRequest request) throws Exception {
        Callable<String> callable = () ->
        {
            String path = safteGetRequestParameter(request, "path");
            String token = safteGetRequestParameter(request, "token");
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            if (path.equals("") && token.equals("")) {
                return getPreviewInfoResult(false, 0, null, "", "参数错误!");
            }
            if (!token.equals("")) {
                DownloadFileTokens fileToken = fileTokenDao.getInfo(employeeInfo.getEa(), token, employeeInfo.getSourceUser());
                if (fileToken != null && fileToken.getFileType().toLowerCase().equals("preview")) {
                    {
                        path = fileToken.getFilePath() == null ? "" : fileToken.getFilePath().trim();
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
            PreviewInfo previewInfo = getPreviewInfo(employeeInfo, path);
            if (previewInfo == null || previewInfo.getPageCount() == 0) {
                return getPreviewInfoResult(false, 0, null, "", "该文件不可以预览!");
            }
            return getPreviewInfoResult(true, previewInfo.getPageCount(), previewInfo.getSheetNames(), path, "");
        };
        return new WebAsyncTask<>(1000 * 60, callable);
    }


    @RequestMapping(value = "/preview/getFilePath")
    public WebAsyncTask convert(HttpServletRequest request) throws Exception {
        Callable<ModelAndView> callable = () -> {
            String path = safteGetRequestParameter(request, "path");
            String page = safteGetRequestParameter(request, "page");
            String name = safteGetRequestParameter(request, "name");
            int pageIndex = page.isEmpty() ? 0 : Integer.parseInt(page);
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            String ea = employeeInfo.getEa();
            PreviewInfo previewInfo = getPreviewInfo(employeeInfo, path);
            if (previewInfo != null) {
                DataFileInfo dataFileInfo = previewInfoDao.getDataFileInfo(ea, path, pageIndex, previewInfo);
                if (!dataFileInfo.getShortFilePath().equals("")) {
                    return handModelAndView(dataFileInfo.getShortFilePath());

                } else {
                    String originalFilePath = dataFileInfo.getOriginalFilePath();
                    String dataFilePath = docConvertor.doConvert(path, dataFileInfo.getDataDir(), name, originalFilePath, pageIndex);
                    if (!Strings.isNullOrEmpty(dataFilePath)) {
                        previewInfoDao.savePreviewInfo(ea, path, dataFilePath);
                    }
                    return handModelAndView(dataFilePath);
                }
            } else {
                return handModelAndView("");
            }
        };
        return new WebAsyncTask(1000 * 60, callable);
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

    @ResponseBody
    @RequestMapping(value = "/preview/DocPreviewByPath", method = RequestMethod.GET)
    public void docPreviewByPath(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = safteGetRequestParameter(request, "npath") == "" ? safteGetRequestParameter(request, "path") : safteGetRequestParameter(request, "path");
        if (Strings.isNullOrEmpty(path)) {
            response.setStatus(400);
        } else {
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            DocPreviewInfo docPreviewInfo = getDocPreviewInfo(employeeInfo, path);
            if (docPreviewInfo == null) {
                response.setStatus(404);
            } else {
                int pageCount = docPreviewInfo.getPageCount();
                DocPageInfo docPageInfo = DocPageInfoHelper.GetDocPageInfo(path);
                docPageInfo.setPageCount(pageCount);
                response.setStatus(200);
                response.setContentType("application/json");
                String json = JSON.toJSONString(docPageInfo);
                PrintWriter printWriter = response.getWriter();
                printWriter.write(json);
            }
        }
    }

    /*
      培训助手预览
     */
    private DocPreviewInfo getDocPreviewInfo(EmployeeInfo employeeInfo, String path) throws Exception {
        String ea = employeeInfo.getEa();
        int employeeId = employeeInfo.getEmployeeId();
        DocPreviewInfo docPreviewInfo = docPreviewInfoDao.getInfoByPath(ea, path);
        int pageCount;
        List<String> sheetNames;
        if (docPreviewInfo == null) {
            byte[] bytes = fileStorageProxy.GetBytesByPath(path, employeeInfo, "");
            if (bytes != null && bytes.length > 0) {
                String extension = FilenameUtils.getExtension(path).toLowerCase();
                String dataDir = new PathHelper(ea).getDataDir();
                String fileName = SampleUUID.getUUID() + "." + extension;
                String filePath = FilenameUtils.concat(dataDir, fileName);
                //下载下来保存便于文档转换方便 // TODO: 2016/11/10 当所有的页码都转码完毕后需要删除.
                FileUtils.writeByteArrayToFile(new File(filePath), bytes);
                PageInfo pageInfo = DocPageInfoHelper.GetPageInfo(bytes, filePath);
                pageCount = pageInfo.getPageCount();
                sheetNames = pageInfo.getSheetNames();
                docPreviewInfo = docPreviewInfoDao.initDocPreviewInfo(ea, employeeId, path, filePath, dataDir, bytes.length, pageCount, sheetNames);
            }
        }
        return docPreviewInfo;
    }

    /*
    手机预览
     */
    private PreviewInfo getPreviewInfo(EmployeeInfo employeeInfo, String path) throws Exception {
        String ea = employeeInfo.getEa();
        int employeeId = employeeInfo.getEmployeeId();
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        int pageCount;
        List<String> sheetNames;
        if (previewInfo == null) {
            byte[] bytes = fileStorageProxy.GetBytesByPath(path, employeeInfo, "");
            if (bytes != null && bytes.length > 0) {
                String extension = FilenameUtils.getExtension(path).toLowerCase();
                String dataDir = new PathHelper(ea).getDataDir();
                String fileName = SampleUUID.getUUID() + "." + extension;
                String filePath = FilenameUtils.concat(dataDir, fileName);
                //下载下来保存便于文档转换方便 // TODO: 2016/11/10 当所有的页码都转码完毕后需要删除.
                FileUtils.writeByteArrayToFile(new File(filePath), bytes);
                PageInfo pageInfo = DocPageInfoHelper.GetPageInfo(bytes, filePath);
                pageCount = pageInfo.getPageCount();
                sheetNames = pageInfo.getSheetNames();
                previewInfo = previewInfoDao.initPreviewInfo(ea, employeeId, path, filePath, dataDir, bytes.length, pageCount, sheetNames);
            }
        }
        return previewInfo;
    }

    @ResponseBody
    @RequestMapping(value = "/preview/DocPageByPath", method = RequestMethod.GET)
    public void docPageByPath(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = safteGetRequestParameter(request, "npath") == "" ? safteGetRequestParameter(request, "path") : safteGetRequestParameter(request, "npath");
        int pageIndex = NumberUtils.toInt(safteGetRequestParameter(request, "pageIndex"), 0);
        int width = NumberUtils.toInt(safteGetRequestParameter(request, "width"), 1136);
        width = width > 1920 ? 1920 : width;
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String ea = employeeInfo.getEa();
        DocPreviewInfo docPreviewInfo = getDocPreviewInfo(employeeInfo, path);
        if (docPreviewInfo != null) {
            DataFileInfo dataFileInfo = docPreviewInfoDao.getDataFileInfo(ea, path, pageIndex, docPreviewInfo);
            if (!dataFileInfo.getShortFilePath().equals("")) {
                responseBinary(dataFileInfo.getShortFilePath(), response);
            } else {
                String originalFilePath = dataFileInfo.getOriginalFilePath();
                String dataFilePath = docConvertor.doConvert(path, dataFileInfo.getDataDir(), "", originalFilePath, pageIndex, width);
                if (!Strings.isNullOrEmpty(dataFilePath)) {
                    docPreviewInfoDao.saveDocPreviewInfo(ea, path, dataFilePath);
                    responseBinary(dataFilePath, response);
                } else {
                    logger.error("ea:{},path:{} do convert hanppend error!", ea, path);
                    response.setStatus(500);
                }
            }
        } else {
            response.setStatus(400);
        }
    }

    private void responseBinary(String dataFilePath, HttpServletResponse response) throws IOException {
        String[] array = dataFilePath.split("/");
        String folder = array[0];
        String fileName = array[1];
        String baseDir = docPreviewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/" + fileName;
        FileChannel fc = new RandomAccessFile(filePath, "r").getChannel();
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        byte[] buffer = new byte[(int) fc.size()];
        mbb.get(buffer);
        OutputStream out = response.getOutputStream();
        out.write(buffer);
        out.flush();
        out.close();
        mbb.force();
        fc.close();
    }

    private String safteGetRequestParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName) == null ? "" : request.getParameter(paramName).trim();
        return value;
    }

    private String getPreviewInfoResult(boolean canPreview, int pageCount, List<String> sheetNames, String path, String errorMsg) {
        Map<String, Object> map = new HashMap<>();
        map.put("canPreview", canPreview);
        if (canPreview) {
            map.put("pageCount", pageCount);
            map.put("path", path);
            map.put("sheets", sheetNames);
        } else
            map.put("errorMsg", errorMsg);
        return JSONObject.toJSONString(map);
    }

    @ExceptionHandler
    @ResponseBody
    public void handleException(HttpServletResponse req, Exception e) {
        logger.error("error:", e);
        req.setStatus(500);
    }
}

