package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.ImageSize;
import com.facishare.document.preview.cgi.utils.ImageHandler;
import com.facishare.document.preview.cgi.utils.ThumbnailSizeHelper;
import com.fxiaoke.common.image.SimpleImageInfo;
import com.google.common.base.Strings;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
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
            OutputStream out = response.getOutputStream();
            byte[] buffer;
            try {
                if (fileName.contains(".svg")) {
                    buffer = handleSvg(filePath, width, response);
                } else if (fileName.contains(".png")) {
                    buffer = handlePng(filePath, width, response);
                } else {
                    buffer = handleFile(filePath, response);
                }
                out.write(buffer);
            } catch (Exception ex) {
                logger.error("filepath:{}", filePath, ex);
                response.setStatus(404);
            } finally {
                out.flush();
                out.close();
            }
        }
    }

    private byte[] handleFile(String filePath,HttpServletResponse response) throws IOException {
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
        return FileUtils.readFileToByteArray(new File(filePath));
    }

    private byte[] handleSvg(String filePath, int width,HttpServletResponse response) throws IOException, TranscoderException {
        if (width == 0) {
            response.setContentType("image/svg+xml");
            return FileUtils.readFileToByteArray(new File(filePath));
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String svgFileName = FilenameUtils.getName(filePath);
        String jpgFilePath = FilenameUtils.concat(FilenameUtils.getFullPath(filePath), getFileNameNoEx(svgFileName) + ".jpg");
        File jpgFile = new File(jpgFilePath);
        if (!jpgFile.exists()) {
            ImageHandler.convertSvgToJpg(filePath, jpgFilePath);
        }
        //缩略
        SimpleImageInfo simpleImageInfo = new SimpleImageInfo(jpgFile);
        int height = width * simpleImageInfo.getHeight() / simpleImageInfo.getWidth();
        Thumbnails.of(jpgFile).forceSize(width, height).outputQuality(0.8).outputFormat("jpg").toOutputStream(outputStream);
        response.setContentType("image/jpeg");
        return outputStream.toByteArray();
    }


    private byte[] handlePng(String filePath, int width,HttpServletResponse response) throws IOException, TranscoderException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //缩略:如果制定大小就从原图中缩略到指定大小，如果不指定大小生成固定大小给手机预览使用
        response.setContentType("image/jpeg");
        if (width > 0) {
            SimpleImageInfo simpleImageInfo = new SimpleImageInfo(new File(filePath));
            int height = width * simpleImageInfo.getHeight() / simpleImageInfo.getWidth();
            Thumbnails.of(filePath).forceSize(width, height).outputQuality(0.8).outputFormat("jpg").toOutputStream(outputStream);
            return outputStream.toByteArray();
        } else {
            ImageSize imageSize = ThumbnailSizeHelper.getProcessedSize(filePath);
            String pngFilePath = FilenameUtils.getName(filePath);
            String jpgFilePath = FilenameUtils.getPath(filePath) + "/" + getFileNameNoEx(pngFilePath) + "fixed.jpg";
            File jpgFile = new File(jpgFilePath);
            if (!jpgFile.exists()) {
                Thumbnails.of(filePath).size(imageSize.getWidth(), imageSize.getHeight()).outputQuality(0.8).outputFormat("jpg").toFile(jpgFile);
            }
            return FileUtils.readFileToByteArray(jpgFile);
        }
    }

    private String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
}
