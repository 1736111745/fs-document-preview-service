package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.utils.ImageHandler;
import com.facishare.document.preview.common.utils.MimeTypeHelper;
import com.fxiaoke.common.image.SimpleImageInfo;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by liuq on 16/9/29.
 */

@Controller
@RequestMapping("/")
@Slf4j
public class ResourceController {
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
                response.setContentLength(buffer.length);
                out.write(buffer);
            } catch (Exception ex) {
                log.error("filepath:{}", filePath, ex);
                response.setStatus(404);
            } finally {
                out.flush();
                out.close();
            }
        }
    }

    private byte[] handleFile(String filePath, HttpServletResponse response) throws IOException {
        String fileName = FilenameUtils.getName(filePath);
        fileName = fileName.toLowerCase();
        String ext = FilenameUtils.getExtension(fileName);
        String mime = MimeTypeHelper.getMimeType(ext);
        response.setContentType(mime);
        return FileUtils.readFileToByteArray(new File(filePath));
    }

    private byte[] handleSvg(String filePath, int width, HttpServletResponse response) throws IOException, TranscoderException {
        if (width == 0) {
            response.setContentType("image/svg+xml");
            return FileUtils.readFileToByteArray(new File(filePath));
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String svgFileName = FilenameUtils.getName(filePath);
        String pngFilePath = FilenameUtils.concat(FilenameUtils.getFullPathNoEndSeparator(filePath), getFileNameNoEx(svgFileName) + ".png");
        File pngFile = new File(pngFilePath);
        if (!pngFile.exists()) {
            ImageHandler.convertSvgToPng(filePath, pngFilePath);
        }
        //缩略
        SimpleImageInfo simpleImageInfo = new SimpleImageInfo(pngFile);
        int height = width * simpleImageInfo.getHeight() / simpleImageInfo.getWidth();
        Thumbnails.of(pngFile).forceSize(width, height).outputFormat("png").toOutputStream(outputStream);
        response.setContentType("image/png");
        return outputStream.toByteArray();
    }


    private byte[] handlePng(String filePath, int width, HttpServletResponse response) throws IOException, TranscoderException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //缩略:如果制定大小就从原图中缩略到指定大小，如果不指定大小生成固定大小给手机预览使用
        SimpleImageInfo simpleImageInfo = new SimpleImageInfo(new File(filePath));
        response.setContentType("image/png");
        if (width > 0) {
            int height = width * simpleImageInfo.getHeight() / simpleImageInfo.getWidth();
            Thumbnails.of(filePath).forceSize(width, height).outputQuality(0.8).outputFormat("png").toOutputStream(outputStream);
            return outputStream.toByteArray();
        } else {
            int fixedWidth = 800;
            int fixedHeight = fixedWidth * simpleImageInfo.getHeight() / simpleImageInfo.getWidth();
            String pngFileName = FilenameUtils.getName(filePath);
            String pngFixedFilePath = FilenameUtils.concat(FilenameUtils.getFullPathNoEndSeparator(filePath), getFileNameNoEx(pngFileName) + "fixed.png");
            File pngFixedFile = new File(pngFixedFilePath);
            if (!pngFixedFile.exists()) {
                Thumbnails.of(filePath).forceSize(fixedWidth, fixedHeight).outputQuality(0.8).outputFormat("png").toFile(pngFixedFile);
            }
            return FileUtils.readFileToByteArray(pngFixedFile);
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
