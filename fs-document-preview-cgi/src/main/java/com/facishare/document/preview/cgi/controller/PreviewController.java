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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.ModelAndView;

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
    public WebAsyncTask<String> getPreviewInfo(HttpServletRequest request) throws Exception {

        Callable<String> callable = () ->
        {
            String path = safteGetRequestParameter(request, "path");
            String name = safteGetRequestParameter(request, "token");
            String token = safteGetRequestParameter(request, "token");
            String securityGroup = "";
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            if (path.equals("") && token.equals("")) {
                return getPreviewInfoResult(false, 0, "", "参数错误!");
            }
            if (!token.equals("")) {
                DownloadFileTokens fileToken = fileTokenDao.getInfo(employeeInfo.getEa(), token, employeeInfo.getSourceUser());
                if (fileToken != null && fileToken.getFileType().toLowerCase().equals("preview")) {
                    {
                        LOG.info("token info:{}", JSONObject.toJSONString(fileToken));
                        path = fileToken.getFilePath() == null ? "" : fileToken.getFilePath().trim();
                        securityGroup = fileToken.getDownloadSecurityGroup();
                        name = fileToken.getFileName() == null ? "" : fileToken.getFileName().trim();
                    }
                }
            }
            if (path.isEmpty()) {
                return getPreviewInfoResult(false, 0, "", "参数错误!");
            }
            String extension = FilenameUtils.getExtension(path).toLowerCase();
            if (allowPreviewExtension.indexOf(extension) == -1) {
                return getPreviewInfoResult(false, 0, "", "该文件不可以预览!");
            }
            PreviewInfo previewInfo = previewInfoDao.getInfoByPath(path);
            int pageCount;
            if (previewInfo == null) {
                try {
                    LOG.info("begin download from warehouse,path:{}", path);
                    byte[] bytes = fileStorageProxy.GetBytesByPath(path, employeeInfo, securityGroup);
                    LOG.info("end download from warehouse ,path:{}", path);
                    if (bytes == null || bytes.length == 0) {
                        return getPreviewInfoResult(false, 0, "", "文件无法找到或者损坏!");
                    }
                    String dataDir = new PathHelper(employeeInfo.getEa()).getDataDir();
                    String fileName = SampleUUID.getUUID() + "." + extension;
                    String filePath = FilenameUtils.concat(dataDir, fileName);
                    FileUtils.writeByteArrayToFile(new File(filePath), bytes);
                    LOG.info("begin get page count,path:{}", path);
                    pageCount = DocPageCalculator.GetDocPageCount(bytes, filePath);
                    LOG.info("end get page count,path:{}", path);
                    previewInfoDao.initPreviewInfo(path, filePath, dataDir, bytes.length, pageCount, employeeInfo.getEa(), employeeInfo.getEmployeeId());
                    return getPreviewInfoResult(true, pageCount, path, "");
                } catch (Exception ex) {
                    LOG.error("get page count", ex);
                    return getPreviewInfoResult(false, 0, "", "该文件不可以预览!");
                }
            }
            pageCount = previewInfo.getPageCount();
            if (pageCount == 0) {
                return getPreviewInfoResult(false, 0, "", "该文件不可以预览!");
            }
            return getPreviewInfoResult(true, pageCount, path, "");
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

    private ModelAndView handModelAndView(String dataFilePath) {
        if (dataFilePath.length() > 0) {
            String[] array = dataFilePath.split("/");
            if (array.length == 2) {
                return new ModelAndView("redirect:/preview/" + array[0] + "/" + array[1]);
            } else
                return new ModelAndView("redirect:/preview/static/pixel.gif");
        } else {
            return new ModelAndView("redirect:/preview/static/pixel.gif");
        }
    }

    @RequestMapping("/preview/{folder}/{fileName:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String baseDir = previewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/" + fileName;
        outPut(response, filePath);
    }

    @RequestMapping("/preview/js/{fileName:.+}")
    public String getStatic(@PathVariable String fileName) throws IOException {
        return "redirect:/static/yozo/" + fileName;
    }

    @RequestMapping("/preview/{folder}/js/{fileName:.+}")
    public void getCss(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String baseDir = previewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/js/" + fileName;
        outPut(response, filePath);
    }

    private void outPut(HttpServletResponse response, String filePath) throws IOException {
        if (filePath.toLowerCase().contains(".png")) {
            response.setContentType("image/png");
        } else if (filePath.toLowerCase().contains(".jpg")) {
            response.setContentType("image/jpeg ");
        } else if (filePath.toLowerCase().contains(".js")) {
            response.setContentType("application/javascript");
        } else if (filePath.toLowerCase().contains(".css")) {
            response.setContentType("text/css");
        } else if (filePath.toLowerCase().contains(".svg")) {
            response.setContentType("image/svg+xml");
        } else if (filePath.toLowerCase().contains(".htm")) {
            response.setContentType("text/html");
        }

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

    @ExceptionHandler
    @ResponseBody
    public void handleException(HttpServletResponse req, Exception e) {
        LOG.error("error:", e);
        req.setStatus(500);
    }
}

