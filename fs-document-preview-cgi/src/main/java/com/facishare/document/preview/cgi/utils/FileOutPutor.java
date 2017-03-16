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
public class FileOutPutor {
    public static void outPut(HttpServletResponse response, String filePath, boolean needThumbnail) throws IOException {
        outPut(response, filePath, 0, needThumbnail);
    }

    public static void outPut(HttpServletResponse response, String filePath, int width, boolean needThumbnail) throws IOException {
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
                    buffer = handlePng(filePath, width, needThumbnail, response);
                    response.setHeader("Cache-Control", "max-age=315360000"); // HTTP/1.1
                } else {
                    buffer = handleFile(filePath, response);
                }
                response.setContentLength(buffer.length);
                out.write(buffer);
            } catch (Exception ex) {
                log.error("filePath:{}", filePath, ex);
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

    private static byte[] handlePng(String filePath, int width, boolean needThumbnail, HttpServletResponse response) throws IOException {
        //缩略:如果制定大小就从原图中缩略到指定大小，如果不指定大小生成固定大小给手机预览使用
        SimpleImageInfo simpleImageInfo = new SimpleImageInfo(new File(filePath));
        response.setContentType("image/png");
        if (width == 0 && !needThumbnail) {
            return FileUtils.readFileToByteArray(new File(filePath));
        }
        int defaultWidth = 750;//手机文档预览使用
        int aimWidth = defaultWidth;
        if (width > 0) {
            aimWidth = width;
        }
        int aimHeight = aimWidth * simpleImageInfo.getHeight() / simpleImageInfo.getWidth();
        String pngFileName = FilenameUtils.getName(filePath);
        String thumbPngFileName = FilenameUtils.getBaseName(pngFileName) + "_" + aimWidth + "x" + aimHeight + ".png";
        String thumbPngFilePath = FilenameUtils.concat(FilenameUtils.getFullPathNoEndSeparator(filePath), thumbPngFileName);
        File thumbPngFile = new File(thumbPngFilePath);
        if (!thumbPngFile.exists()) {
            Thumbnails.of(filePath).forceSize(aimWidth, aimHeight).outputQuality(0.8).outputFormat("png").toFile(thumbPngFile);
        }
        return FileUtils.readFileToByteArray(thumbPngFile);
    }
}