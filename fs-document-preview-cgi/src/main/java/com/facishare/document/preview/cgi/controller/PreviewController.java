package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.cgi.convertor.DocConvertor;
import com.facishare.document.preview.cgi.dao.FileTokenDao;
import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.DataFileInfo;
import com.facishare.document.preview.cgi.model.DownloadFileTokens;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewWayEntity;
import com.facishare.document.preview.cgi.utils.DocPageCalculator;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.fxiaoke.release.FsGrayRelease;
import com.fxiaoke.release.FsGrayReleaseBiz;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
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
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

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
    public String getPreviewInfo(HttpServletRequest request) throws Exception {
        String path = safteGetRequestParameter(request, "path");
        String token = safteGetRequestParameter(request, "token");
        String name = safteGetRequestParameter(request, "name");
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        Map<String, Object> map = new HashMap<>();
        if (path.equals("") && token.equals("")) {
            map.put("canPreview", false);
            map.put("errorMsg", "参数错误!");
            return JSONObject.toJSONString(map);
        }
        if (!token.equals("")) {
            DownloadFileTokens fileToken = fileTokenDao.getInfo(employeeInfo.getEa(), token, employeeInfo.getSourceUser());
            if (fileToken != null && fileToken.getFileType().toLowerCase().equals("preview")) {
                {
                    path = fileToken.getFilePath() == null ? "" : fileToken.getFilePath().trim();
                    name = fileToken.getFileName() == null ? "" : fileToken.getFileName().trim();
                }
            }
        }
        if (path.isEmpty()) {
            map.put("canPreview", false);
            map.put("errorMsg", "参数错误!");
            return JSONObject.toJSONString(map);
        }
        String extension = FilenameUtils.getExtension(path).toLowerCase();
        if (allowPreviewExtension.indexOf(extension) == -1) {
            map.put("canPreview", false);
            map.put("errorMsg", "该文件不可以预览!");
            return JSONObject.toJSONString(map);
        }
        int pageCount = previewInfoDao.getPageCount(path);
        if (pageCount == 0) {
            try {
                byte[] bytes = fileStorageProxy.GetBytesByPath(path, employeeInfo);
                if (bytes == null || bytes.length == 0) {
                    map.put("canPreview", false);
                    map.put("errorMsg", "文件无法找到或者损坏!");
                    return JSONObject.toJSONString(map);
                }
                pageCount = DocPageCalculator.GetDocPageCount(bytes, path);
            } catch (Exception ex) {
                LOG.error("get page count", ex);
                map.put("canPreview", false);
                map.put("errorMsg", "该文件不可以预览!");
                return JSONObject.toJSONString(map);
            }
        }
        if (pageCount == 0) {
            map.put("canPreview", false);
            map.put("errorMsg", "该文件不可以预览!");
            return JSONObject.toJSONString(map);
        }
        name = name.equals("") ? path : name;
        int type = extension.equals("pdf") ? 2 : 1;
        map.put("canPreview", true);
        map.put("pageCount", pageCount);
        map.put("path", path);
        map.put("type", type);
        return JSONObject.toJSONString(map);
    }


    @ResponseBody
    @RequestMapping(value = "/preview/getFilePath", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String convert(HttpServletRequest request) throws Exception {
        String path = safteGetRequestParameter(request, "path");
        String page = safteGetRequestParameter(request, "page");
        String name = safteGetRequestParameter(request, "name");
        String pageCount = safteGetRequestParameter(request, "pageCount");
        int pageCnt = pageCount.isEmpty() ? 0 : Integer.parseInt(pageCount);
        int pageIndex = page.isEmpty() ? 0 : Integer.parseInt(page);
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        DataFileInfo dataFileInfo = previewInfoDao.getDataFileInfo(path, pageIndex, employeeInfo.getEa());
        if (!dataFileInfo.getFilePath().equals("")) {
            Map<String, Object> map = new HashMap<>();
            map.put("filePath", dataFileInfo.getFilePath());
            map.put("successed", true);
            return JSONObject.toJSONString(map);

        } else {
            byte[] bytes = fileStorageProxy.GetBytesByPath(path, employeeInfo);
            String dataFilePath = docConvertor.doConvert(employeeInfo.getEa(), path, dataFileInfo.getBaseDir(), name, bytes, pageIndex);
            if (!dataFilePath.equals("")) {
                previewInfoDao.create(path, dataFileInfo.getBaseDir(), dataFilePath, employeeInfo.getEa(), employeeInfo.getEmployeeId(), bytes.length, pageCnt);
                Map<String, Object> map = new HashMap<>();
                map.put("filePath", dataFilePath);
                map.put("successed", true);
                return JSONObject.toJSONString(map);
            } else {
                LOG.warn("path:{} can't do preview", path);
                Map<String, Object> map = new HashMap<>();
                map.put("successed", false);
                return JSONObject.toJSONString(map);
            }
        }
    }

    @RequestMapping("/preview/{folder}/{fileName:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String baseDir = previewInfoDao.getDataFileInfo(folder);
        String filePath = baseDir + "/" + fileName;
        if (fileName.toLowerCase().contains(".jpg")) {
            response.setContentType("image/jpg");
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
}

