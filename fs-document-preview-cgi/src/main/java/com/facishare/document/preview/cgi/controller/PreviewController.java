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
@RequestMapping("/dps")
public class PreviewController {
    @Autowired
    FileStorageProxy fileStorageProxy;
    @Autowired
    PreviewInfoDao dao;
    private static final Logger LOG = LoggerFactory.getLogger(PreviewController.class);

    @ReloadableProperty("allowPreviewExtension")
    private String allowPreviewExtension = "doc|docx|xls|xlsx|ppt|pptx|pdf";

    @RequestMapping("/preview")
    public void doPreview(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String filePath = request.getParameter("path").trim();
        String fileName = request.getParameter("name").trim();
        String extension = FilenameUtils.getExtension(filePath);
        if (allowPreviewExtension.indexOf(extension) == -1) {
            response.setStatus(403);
            return;
        }
        fileName = (fileName == "" || fileName == null) ? filePath : fileName;
        LOG.info("begin preview,filePath:{},fileName:{}", filePath, fileName);
        //检查下服务器上是否转换过
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String htmlFilePath = "";
        PreviewInfo previewInfo = dao.getInfo(filePath, 1);
        if (previewInfo != null) {
            htmlFilePath = previewInfo.getHtmlFilePath();
            outPut(response, htmlFilePath);
        } else {
            byte[] bytes = fileStorageProxy.GetBytesByPath(filePath, employeeInfo);
            if (bytes == null) {
                LOG.warn("can't get bytes from path:{}", filePath);
                response.setStatus(404);
                return;
            }
            ConvertorHelper convertorHelper = new ConvertorHelper(employeeInfo);
            htmlFilePath = convertorHelper.doConvert(filePath, bytes, fileName);
            if (htmlFilePath != "") {
                dao.create(htmlFilePath, employeeInfo.getEa(), employeeInfo.getEmployeeId(), bytes.length);
                outPut(response, htmlFilePath);
            } else {
                LOG.warn("path:{} can't do preview", filePath);
                response.setStatus(500);
                return;
            }
        }
    }

    @RequestMapping("/{folder}.files/{fileName:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String htmlName = folder;
        PreviewInfo previewInfo = dao.getInfo(htmlName, 0);
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

