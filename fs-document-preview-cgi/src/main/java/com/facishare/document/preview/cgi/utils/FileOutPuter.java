package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.common.utils.MimeTypeHelper;
import com.fxiaoke.common.image.SimpleImageInfo;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by liuq on 2016/12/17.
 */
@Slf4j
@Component
public class FileOutPuter {

    public  static  void outPut(HttpServletResponse response, String filePath) throws IOException {
        outPut(response, filePath, 0);
    }

    public static void outPut(HttpServletResponse response, String filePath, int width) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            response.setStatus(404);
        } else {
            String fileName = FilenameUtils.getName(filePath);
            fileName = fileName.toLowerCase();
            OutputStream out = response.getOutputStream();
            byte[] buffer;
            try {
               if (fileName.contains(".png")) {
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

    private static byte[] handleFile(String filePath, HttpServletResponse response) throws IOException {
        String fileName = FilenameUtils.getName(filePath);
        fileName = fileName.toLowerCase();
        String ext = FilenameUtils.getExtension(fileName);
        String mime = MimeTypeHelper.getMimeType(ext);
        response.setContentType(mime);
        return FileUtils.readFileToByteArray(new File(filePath));
    }

    private static byte[] handlePng(String filePath, int width, HttpServletResponse response) throws IOException {
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
            String pngFixedFilePath = FilenameUtils.concat(FilenameUtils.getFullPathNoEndSeparator(filePath), FilenameUtils.getBaseName(pngFileName) + "fixed.png");
            File pngFixedFile = new File(pngFixedFilePath);
            if (!pngFixedFile.exists()) {
                Thumbnails.of(filePath).forceSize(fixedWidth, fixedHeight).outputQuality(0.8).outputFormat("png").toFile(pngFixedFile);
            }
            return FileUtils.readFileToByteArray(pngFixedFile);
        }
    }
}
