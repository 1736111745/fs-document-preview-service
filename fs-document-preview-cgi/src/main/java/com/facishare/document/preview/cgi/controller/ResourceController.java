package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by liuq on 16/9/29.
 */
@Controller
@RequestMapping("/")
public class ResourceController {
    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
    @Autowired
    PreviewInfoDao previewInfoDao;

    @RequestMapping("/preview/js/{fileName:.+}")
    public String getStatic(@PathVariable String fileName) throws IOException {
        return "redirect:/static/common/" + fileName;
    }

    @RequestMapping("/preview/{folder}/js/{fileName:.+}")
    public void getCss(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String baseDir = previewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/js/" + fileName;
        response.setHeader("Cache-Control", "max-age=315360000"); // HTTP/1.1
        outPut(response, filePath);
    }

    @RequestMapping("/preview/{folder}/{fileName:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String baseDir = previewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/" + fileName;
        response.setHeader("Cache-Control", "max-age=315360000"); // HTTP/1.1
        outPut(response, filePath);
    }

    /*
      处理静态文件
     */
    private void outPut(HttpServletResponse response, String filePath) throws IOException {
        String fileName = FilenameUtils.getName(filePath);
        fileName = fileName.toLowerCase();
        if (fileName.contains(".png")) {
            response.setContentType("image/png");
        } else if (fileName.contains(".jpg")) {
            response.setContentType("image/jpeg ");
        } else if (fileName.contains(".js")) {
            response.setContentType("application/javascript");
        } else if (fileName.contains(".css")) {
            response.setContentType("text/css");
        } else if (fileName.contains(".svg")) {
            response.setContentType("image/svg+xml");
        } else if (fileName.contains(".htm")) {
            response.setContentType("text/html");
        } else if (fileName.contains(".pdf")) {
            response.setContentType("application/pdf");
        }
        try {
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
        } catch (Exception ex) {
            logger.error("filepath:{}", filePath, ex);
        }
    }
}
