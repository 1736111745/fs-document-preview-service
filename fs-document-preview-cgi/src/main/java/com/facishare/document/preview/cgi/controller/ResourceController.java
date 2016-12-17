package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.utils.FileOutPutor;
import com.facishare.document.preview.cgi.utils.RequestParamsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liuq on 16/9/29.
 */

@Controller
@RequestMapping("/")
@Slf4j
public class ResourceController {
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    FileOutPutor fileOutPutor;
    @RequestMapping("/preview/js/{fileName:.+}")
    public String getStatic(@PathVariable String fileName) throws IOException {
        return "redirect:/static/common/" + fileName;
    }

    @RequestMapping("/preview/{folder}/js/{fileName:.+}")
    public void getPreviewStaticContent(@PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) throws IOException {
        String baseDir = previewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/js/" + fileName;
        response.setHeader("Cache- Control", "max-age=315360000"); // HTTP/1.1
        fileOutPutor.outPut(response, filePath, 0);
    }

    @RequestMapping("/preview/{folder}/{fileName:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String width = RequestParamsHelper.safteGetRequestParameter(request, "width");
        int intWidth = NumberUtils.toInt(width, 0);
        String baseDir = previewInfoDao.getBaseDir(folder);
        String filePath = baseDir + "/" + fileName;
        response.setHeader("Cache-Control", "max-age=315360000"); // HTTP/1.1
        fileOutPutor.outPut(response, filePath, intWidth);
    }
}
