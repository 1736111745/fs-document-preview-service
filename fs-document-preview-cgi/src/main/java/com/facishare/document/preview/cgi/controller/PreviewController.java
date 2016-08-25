package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.utils.ConvertorHelper;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
    PreviewInfoDao dao;
    private static final Logger LOG = LoggerFactory.getLogger(PreviewController.class);

    @ReloadableProperty("allowPreviewExtension")
    private String allowPreviewExtension = "doc|docx|xls|xlsx|ppt|pptx|pdf";

    @RequestMapping("/preview")
    public String doPreview(HttpServletRequest request, HttpServletResponse response) {
        return "preview";
    }

    @RequestMapping("/preview/show")
    public void preivew(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = safteGetRequestParameter(request, "path");
        String fileName = safteGetRequestParameter(request, "name");
        if (path == "") {
            response.getWriter().println("参数错误!");
            return;
        }
        String extension = FilenameUtils.getExtension(path);
        if (allowPreviewExtension.indexOf(extension) == -1) {
            response.getWriter().println("该文件不可以预览!");
            return;
        }
        fileName = (fileName == "" || fileName == null) ? path : fileName;
        LOG.info("begin preview,path:{},fileName:{}", path, fileName);
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        PreviewInfo previewInfo = dao.getInfoByPath(path);
        if (previewInfo == null || previewInfo.getHtmlFilePath() == "") {
            byte[] bytes = fileStorageProxy.GetBytesByPath(path, employeeInfo);
            if (bytes == null) {
                response.getWriter().println("该文件找不到或者损坏!");
                return;
            }
            ConvertorHelper convertorHelper = new ConvertorHelper(employeeInfo);
            String htmlFilePath = convertorHelper.doConvert(path, bytes, fileName);
            if (htmlFilePath != "") {
                dao.create(path, htmlFilePath, employeeInfo.getEa(), employeeInfo.getEmployeeId(), bytes.length);
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

    @RequestMapping("/preview/{folder}.files/{fileName:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String htmlName = folder;
        PreviewInfo previewInfo = dao.getInfoByHtmlName(htmlName);
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

    private String safteGetRequestParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName) == null ? "" : request.getParameter(paramName).trim();
        return value;
    }
}

