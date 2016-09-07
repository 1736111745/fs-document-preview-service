package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.cgi.dao.FileTokenDao;
import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.*;
import com.facishare.document.preview.cgi.utils.ConvertorHelper;
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
    @RequestMapping(value = "/preview/getPageCount", method = RequestMethod.GET)
    public int getPageCount(HttpServletRequest request) throws Exception {
        String path = safteGetRequestParameter(request, "path");
        String token = safteGetRequestParameter(request, "token");
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        if (!token.equals("")) {
            DownloadFileTokens fileToken = fileTokenDao.getInfo(employeeInfo.getEa(), token, employeeInfo.getSourceUser());
            if (fileToken != null && fileToken.getFileType().toLowerCase().equals("preview")) {
                {
                    path = fileToken.getFilePath() == null ? "" : fileToken.getFilePath().trim();
                }
            }
        }
        if (path.isEmpty())
            return -1;
        else {
            int pageCount = previewInfoDao.getPageCount(path);
            if (pageCount == 0) {
                byte[] bytes = fileStorageProxy.GetBytesByPath(path, employeeInfo);
                ConvertorHelper convertorHelper = new ConvertorHelper(employeeInfo);
                pageCount = convertorHelper.getPageCount(path, bytes);
            }
            return pageCount;
        }
    }


    @ResponseBody
    @RequestMapping(value = "/preview/getsvg", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String convert(HttpServletRequest request) throws Exception {
        String path = safteGetRequestParameter(request, "path");
        String page = safteGetRequestParameter(request, "page");
        String token = safteGetRequestParameter(request, "token");
        String name = safteGetRequestParameter(request, "name");
        int pageIndex = page.isEmpty() ? 0 : Integer.parseInt(page);
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        if (path.equals("") && token.equals("")) {
            jsonResponseEntity.setSuccessed(false);
            jsonResponseEntity.setErrMsg("参数错误!");
            return JSONObject.toJSONString(jsonResponseEntity);
        } else {
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            if (!token.equals("")) {
                DownloadFileTokens fileToken = fileTokenDao.getInfo(employeeInfo.getEa(), token, employeeInfo.getSourceUser());
                if (fileToken == null || !fileToken.getFileType().toLowerCase().equals("preview")) {
                    {
                        if (fileToken == null) {
                            LOG.warn("token not exsist!");
                        } else {
                            LOG.warn("token type isn't right!json:{}", JSON.toJSONString(fileToken));
                        }
                        jsonResponseEntity.setSuccessed(false);
                        jsonResponseEntity.setErrMsg("参数错误!");
                        return JSONObject.toJSONString(jsonResponseEntity);
                    }
                } else {
                    path = fileToken.getFilePath();
                    name = name.equals("") ? fileToken.getFileName() : name;
                }
            } else {
                name = name.equals("") ? path : name;
            }
            String extension = FilenameUtils.getExtension(path);
            if (allowPreviewExtension.indexOf(extension) == -1) {
                jsonResponseEntity.setSuccessed(false);
                jsonResponseEntity.setErrMsg("该文件不可以预览!");
                return JSONObject.toJSONString(jsonResponseEntity);
            }
            SvgFileInfo svgFileInfo = previewInfoDao.getSvgBaseDir(path, pageIndex, employeeInfo.getEa());
            if (!svgFileInfo.getFilePath().equals("")) {
                jsonResponseEntity.setSuccessed(true);
                jsonResponseEntity.setSvgFile(svgFileInfo.getFilePath());
                return JSONObject.toJSONString(jsonResponseEntity);
            } else {
                byte[] bytes = fileStorageProxy.GetBytesByPath(path, employeeInfo);
                if (bytes == null) {
                    LOG.warn("can't get bytes from path:{}", path);
                    jsonResponseEntity.setSuccessed(false);
                    jsonResponseEntity.setErrMsg("该文件找不到或者损坏!");
                    return JSONObject.toJSONString(jsonResponseEntity);
                }
                ConvertorHelper convertorHelper = new ConvertorHelper(employeeInfo);
                String svgFilePath = convertorHelper.doConvert(path, svgFileInfo.getBaseDir(), name, bytes, pageIndex);
                if (!svgFilePath.equals("")) {
                    previewInfoDao.create(path, svgFileInfo.getBaseDir(), svgFilePath, employeeInfo.getEa(), employeeInfo.getEmployeeId(), bytes.length);
                    jsonResponseEntity.setSuccessed(true);
                    jsonResponseEntity.setSvgFile(svgFilePath);
                    return JSONObject.toJSONString(jsonResponseEntity);
                } else {
                    LOG.warn("path:{} can't do preview", path);
                    jsonResponseEntity.setSuccessed(false);
                    jsonResponseEntity.setErrMsg("很抱歉,该文件无法预览!");
                    return JSONObject.toJSONString(jsonResponseEntity);
                }
            }
        }
    }

    @RequestMapping("/preview/{folder}/{fileName:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String baseDir = previewInfoDao.getSvgBaseDir(folder);
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
//    private static String getHtml(String filePath) throws IOException {
//        StringBuffer sb = new StringBuffer();
//        File file=new File(filePath);
//        LineIterator it = FileUtils.lineIterator(file, "UTF-8");
//        try {
//            while (it.hasNext()) {
//                String line = it.nextLine();
//                sb.append(line);
//            }
//        } finally {
//            LineIterator.closeQuietly(it);
//        }
//        return sb.toString();
//    }
}

