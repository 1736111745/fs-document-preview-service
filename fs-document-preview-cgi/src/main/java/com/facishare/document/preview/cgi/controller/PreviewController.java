package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.cgi.convertor.DocConvertor;
import com.facishare.document.preview.cgi.dao.FileTokenDao;
import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.*;
import com.facishare.document.preview.cgi.utils.DocPageCalculator;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.cgi.utils.PathHelper;
import com.facishare.document.preview.cgi.utils.SampleUUID;
import com.fxiaoke.release.FsGrayRelease;
import com.fxiaoke.release.FsGrayReleaseBiz;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
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
    private FsGrayReleaseBiz gray = FsGrayRelease.getInstance("dps");

    @ReloadableProperty("allowPreviewExtension")
    private String allowPreviewExtension = "doc|docx|xls|xlsx|ppt|pptx|pdf";


    @RequestMapping(value = "/preview/bypath", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String previewByPath() {
        return "preview";
    }

    @RequestMapping(value = "/preview/bytoken", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String previewByToken() {
        return "preview";
    }

    @RequestMapping(value = "/preview/handleExcel", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String handleExcel() {
        return "preview_excel";
    }

    @ResponseBody
    @RequestMapping(value = "/preview/getPreviewConfig", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getPreviewWay(HttpServletRequest request) {
        PreviewWayEntity entity = new PreviewWayEntity();
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String user = "E." + employeeInfo.getEa() + "." + employeeInfo.getEmployeeId();
        boolean newway = gray.isAllow("newway", user);
        if (newway) {
            entity.setWay(1);
            String byTokenUrl = "/dps/preview/bytoken?token={0}&name={1}";
            String byPathUrl = "/dps/preview/bypath?path={0}&name={1}";
            entity.setPreviewByPathUrlFormat(byPathUrl);
            entity.setPreviewByTokenUrlFormat(byTokenUrl);
        } else
            entity.setWay(0);
        String json = JSON.toJSONString(entity);
        return json;
    }

    @ResponseBody
    @RequestMapping(value = "/preview/getPreviewInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Callable<String> getPreviewInfo(HttpServletRequest request) throws Exception {
        System.out.println("getPreviewInfo被调用 thread id is : " + Thread.currentThread().getId());
        final String[] path = {safteGetRequestParameter(request, "path")};
        String token = safteGetRequestParameter(request, "token");
        final String[] name = {safteGetRequestParameter(request, "name")};
        final String[] securityGroup = {""};
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        return () ->
        {
            System.out.println("getCount thread id is : " + Thread.currentThread().getId());
            if (path[0].equals("") && token.equals("")) {
                return getPreviewInfoResult(false, 0, "", "参数错误!");
            }
            if (!token.equals("")) {
                DownloadFileTokens fileToken = fileTokenDao.getInfo(employeeInfo.getEa(), token, employeeInfo.getSourceUser());
                if (fileToken != null && fileToken.getFileType().toLowerCase().equals("preview")) {
                    {
                        LOG.info("token info:{}", JSONObject.toJSONString(fileToken));
                        path[0] = fileToken.getFilePath() == null ? "" : fileToken.getFilePath().trim();
                        securityGroup[0] = fileToken.getDownloadSecurityGroup();
                        name[0] = fileToken.getFileName() == null ? "" : fileToken.getFileName().trim();
                    }
                }
            }
            if (path[0].isEmpty()) {
                return getPreviewInfoResult(false, 0, "", "参数错误!");
            }
            String extension = FilenameUtils.getExtension(path[0]).toLowerCase();
            if (allowPreviewExtension.indexOf(extension) == -1) {
                return getPreviewInfoResult(false, 0, "", "该文件不可以预览!");
            }
            PreviewInfo previewInfo = previewInfoDao.getInfoByPath(path[0]);
            int pageCount;
            if (previewInfo == null) {
                try {
                    byte[] bytes = fileStorageProxy.GetBytesByPath(path[0], employeeInfo, securityGroup[0]);
                    if (bytes == null || bytes.length == 0) {
                        return getPreviewInfoResult(false, 0, "", "文件无法找到或者损坏!");
                    }
                    String dataDir = new PathHelper(employeeInfo.getEa()).getDataDir();
                    String fileName = SampleUUID.getUUID() + "." + extension;
                    String filePath = FilenameUtils.concat(dataDir, fileName);
                    FileUtils.writeByteArrayToFile(new File(filePath), bytes);
                    pageCount = DocPageCalculator.GetDocPageCount(bytes, filePath);
                    previewInfoDao.initPreviewInfo(path[0], filePath, dataDir, bytes.length, pageCount, employeeInfo.getEa(), employeeInfo.getEmployeeId());
                    return getPreviewInfoResult(true, pageCount, path[0], "");
                } catch (Exception ex) {
                    LOG.error("get page count", ex);
                    return getPreviewInfoResult(false, 0, "", "该文件不可以预览!");
                }
            }
            pageCount = previewInfo.getPageCount();
            if (pageCount == 0) {
                return getPreviewInfoResult(false, 0, "", "该文件不可以预览!");
            }
            return getPreviewInfoResult(true, pageCount, path[0], "");
        };
    }


    @ResponseBody
    @RequestMapping(value = "/preview/getFilePath", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Callable<String> convert(HttpServletRequest request) throws Exception {
        String path = safteGetRequestParameter(request, "path");
        String page = safteGetRequestParameter(request, "page");
        String name = safteGetRequestParameter(request, "name");
        String pageCount = safteGetRequestParameter(request, "pageCount");
        int pageCnt = pageCount.isEmpty() ? 0 : Integer.parseInt(pageCount);
        int pageIndex = page.isEmpty() ? 0 : Integer.parseInt(page);
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        DataFileInfo dataFileInfo = previewInfoDao.getDataFileInfo(path, pageIndex, employeeInfo.getEa());
        return () ->
        {
            if (!dataFileInfo.getShortFilePath().equals("")) {
                return getFilePathResult(true, dataFileInfo.getShortFilePath());

            } else {
                String originalFilePath = dataFileInfo.getOriginalFilePath();
                File file = new File(originalFilePath);
                String dataFilePath = docConvertor.doConvert(employeeInfo.getEa(), path, dataFileInfo.getDataDir(), name, originalFilePath, pageIndex);
                if (!dataFilePath.equals("")) {
                    previewInfoDao.create(path, dataFileInfo.getDataDir(), dataFilePath, employeeInfo.getEa(), employeeInfo.getEmployeeId(), file.length(), pageCnt);
                    return getFilePathResult(true, dataFilePath);
                } else {
                    LOG.warn("path:{} can't do preview", path);
                    return getFilePathResult(false, "");
                }
            }
        };
    }

    @RequestMapping("/preview/{folder}/{fileName:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String baseDir = previewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/" + fileName;
        if (fileName.toLowerCase().contains(".png")) {
            response.setContentType("image/png");
        } else if (fileName.toLowerCase().contains(".js")) {
            response.setContentType("application/javascript");
        } else if (fileName.toLowerCase().contains(".css")) {
            response.setContentType("text/css");
        } else if (fileName.toLowerCase().contains(".svg")) {
            response.setContentType("image/svg+xml");
        }
        outPut(response, filePath);
    }

    //加载excel生成出来的html
    @RequestMapping("/preview/js/{fileName:.+}")
    public String getStatic(@PathVariable String fileName) throws IOException {
        return "redirect:/static/yozo/" + fileName;
    }

    //加载excel生成出来的html的样式
    @RequestMapping("/preview/{folder}/js/{fileName:.+}")
    public void getCss(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String baseDir = previewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/js/" + fileName;
        if (fileName.toLowerCase().contains(".png")) {
            response.setContentType("image/png");
        } else if (fileName.toLowerCase().contains(".js")) {
            response.setContentType("application/javascript");
        } else if (fileName.toLowerCase().contains(".css")) {
            response.setContentType("text/css");
        } else if (fileName.toLowerCase().contains(".svg")) {
            response.setContentType("image/svg+xml");
        }
        outPut(response, filePath);
    }

    private void outPut(HttpServletResponse response, String filePath) throws IOException {
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

    private String getPreviewInfoResult(boolean canPreview, int pageCount, String path, String errorMsg) {
        Map<String, Object> map = new HashMap<>();
        map.put("canPreview", canPreview);
        if (canPreview) {
            map.put("pageCount", pageCount);
            map.put("path", path);
        } else
            map.put("errorMsg", errorMsg);
        return JSONObject.toJSONString(map);
    }

    private String getFilePathResult(boolean successed, String filePath) {
        Map<String, Object> map = new HashMap<>();
        map.put("successed", successed);
        if (!filePath.equals(""))
            map.put("filePath", filePath);
        return JSONObject.toJSONString(map);
    }
}

