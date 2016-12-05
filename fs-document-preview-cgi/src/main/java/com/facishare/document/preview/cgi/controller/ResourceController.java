package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.utils.ImageHandler;
import com.fxiaoke.common.image.SimpleImageInfo;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
    public void getPreviewStaticContent(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String baseDir = previewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/js/" + fileName;
        response.setHeader("Cache-Control", "max-age=315360000"); // HTTP/1.1
        outPut(response, filePath, 0);
    }

    @RequestMapping("/preview/{folder}/{fileName:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String width = safteGetRequestParameter(request, "width");
        int intWidth = NumberUtils.toInt(width, 0);
        String baseDir = previewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/" + fileName;
        response.setHeader("Cache-Control", "max-age=315360000"); // HTTP/1.1
        outPut(response, filePath, intWidth);
    }

    private String safteGetRequestParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName) == null ? "" : request.getParameter(paramName).trim();
        return value;
    }

    /*
      处理静态文件
     */
    private void outPut(HttpServletResponse response, String filePath, int width) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            response.setStatus(404);
        } else {
            String fileName = FilenameUtils.getName(filePath);
            fileName = fileName.toLowerCase();
            response.setContentLength((int) file.length());
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
            FileChannel fc = new RandomAccessFile(filePath, "r").getChannel();
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            OutputStream out = response.getOutputStream();
            try {
                byte[] buffer = new byte[(int) fc.size()];
                mbb.get(buffer);
                if (width > 0) {
                    //缩略
                    SimpleImageInfo simpleImageInfo = new SimpleImageInfo(file);
                    int height = width * simpleImageInfo.getHeight() / simpleImageInfo.getWidth();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    String jpgFilePath=FilenameUtils.getPath(filePath)+"/"+FilenameUtils.getName(fileName)+".jpg";
                    File jpgFile=new File(jpgFilePath);
                    if(!jpgFile.exists()) {
                        ImageHandler.convertSvgToJpg(filePath, jpgFilePath);
                    }
                    Thumbnails.of(jpgFile).forceSize(width, height).outputQuality(0.8).outputFormat("png").toOutputStream(outputStream);
                    buffer = outputStream.toByteArray();
                }
                out.write(buffer);
            } catch (Exception ex) {
                logger.error("filepath:{}", filePath, ex);
            } finally {
                out.flush();
                out.close();
                mbb.force();
                fc.close();
            }
        }
    }
}
