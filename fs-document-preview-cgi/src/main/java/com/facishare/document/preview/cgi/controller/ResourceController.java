package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
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
        outPut(response, filePath);
    }
    @RequestMapping("/preview/{folder}/{fileName:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String baseDir = previewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/" + fileName;
        outPut(response, filePath);
    }
    private void outPut(HttpServletResponse response, String filePath) throws IOException {
        filePath=filePath.toLowerCase();
        if (filePath.contains(".png")) {
            response.setContentType("image/png");
        } else if (filePath.contains(".jpg")) {
            response.setContentType("image/jpeg ");
        } else if (filePath.contains(".js")) {
            response.setContentType("application/javascript");
        } else if (filePath.contains(".css")) {
            response.setContentType("text/css");
        } else if (filePath.contains(".svg")) {
            response.setContentType("image/svg+xml");
        } else if (filePath.contains(".htm")) {
            response.setContentType("text/html");
        } else if(filePath.contains(".pdf")) {
            response.setContentType("application/pdf");
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
}
