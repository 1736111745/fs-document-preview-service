package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.cgi.dao.FileTokenDao;
import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.DownloadFileTokens;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.model.PreviewWayEntity;
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
import java.io.File;
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

    @ResponseBody
    @RequestMapping(value = "/preview/getway", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getPreviewWay(HttpServletRequest request, HttpServletResponse response) {
        PreviewWayEntity entity = new PreviewWayEntity();
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String user = "E." + employeeInfo.getEa() + "." + employeeInfo.getEmployeeId();
        boolean newway = gray.isAllow("newway", user);
        if (newway) {
            entity.setNewWay(true);
            String byTokenUrl = "/dps/prewview/token/{0}";
            String byPathUrl = "/dps/prewview/path/{0}";
            entity.setPreviewByPathUrlFormat(byPathUrl);
            entity.setPreviewByTokenUrlFormat(byTokenUrl);
        } else
            entity.setNewWay(false);
        String json = JSON.toJSONString(entity);
        return json;
    }

    @RequestMapping("/preview/path/{path}")
    public void preivewByPath(@PathVariable String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (path.equals("")) {
            response.getWriter().println("参数错误!");
            return;
        }
        String extension = FilenameUtils.getExtension(path);
        if (allowPreviewExtension.indexOf(extension) == -1) {
            response.getWriter().println("该文件不可以预览!");
            return;
        }
        LOG.info("begin preview by path,path:{}", path);
        doPreview(path, request, response);
    }

    @RequestMapping("/preview/token/{token}")
    public void preivewByToken(@PathVariable String token, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (token.equals("")) {
            response.getWriter().println("参数错误!");
            return;
        }
        LOG.info("begin preview by token,path:{}", token);
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        DownloadFileTokens fileToken = fileTokenDao.getInfo(employeeInfo.getEa(), token, employeeInfo.getSourceUser());
        if (fileToken == null || !fileToken.getFileType().toLowerCase().equals("preview")) {
            {
                if (fileToken == null) {
                    LOG.warn("token not exsist!");
                } else {
                    LOG.warn("token type isn't right!json:{}", JSON.toJSONString(fileToken));
                }
                response.getWriter().println("参数错误!");
                return;
            }
        }
        String path = fileToken.getFilePath();
        doPreview(path, request, response);
    }

    private void doPreview(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(path);
        if (previewInfo == null || previewInfo.getHtmlFilePath().equals("")) {
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            byte[] bytes = fileStorageProxy.GetBytesByPath(path, employeeInfo);
            if (bytes == null) {
                response.getWriter().println("该文件找不到或者损坏!");
                return;
            }
            ConvertorHelper convertorHelper = new ConvertorHelper(employeeInfo);
            String htmlFilePath = convertorHelper.doConvert(path, bytes);
            if (htmlFilePath != "") {
                previewInfoDao.create(path, htmlFilePath, employeeInfo.getEa(), employeeInfo.getEmployeeId(), bytes.length);
                response.setContentType("text/html");
                outPut(response, htmlFilePath);
                return;
            } else {
                LOG.warn("path:{} can't do preview", path);
                response.getWriter().println("很抱歉,该文件不能正常预览!");
                return;
            }
        } else {
            response.setContentType("text/html");
            outPut(response, previewInfo.getHtmlFilePath());
            return;
        }
    }

    @RequestMapping("/preview/*/{folder}.files/{fileName:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String htmlName = folder;
        PreviewInfo previewInfo = previewInfoDao.getInfoByHtmlName(htmlName);
        String htmlFilePath = previewInfo.getHtmlFilePath();
        File file = new File(htmlFilePath);
        String folderName = folder + ".files";
        String parent = file.getParent() + "/" + folderName;
        String filePath = parent + "/" + fileName;
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

}

