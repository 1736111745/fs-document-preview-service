package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.utils.FileOutPutor;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
  public String getStatic(@PathVariable String fileName) {
    return "redirect:/static/common/" + fileName;
  }

  @RequestMapping("/preview/{folder}/js/{fileName:.+}")
  public void getPreviewStaticContent(@PathVariable String folder,
                                      @PathVariable String fileName,
                                      HttpServletResponse response) throws IOException {
    String baseDir = previewInfoDao.getBaseDir(folder);
    if (!Strings.isNullOrEmpty(baseDir)) {
      String filePath = baseDir + "/js/" + fileName;
      fileOutPutor.outPut(response, filePath, false);
    } else {
      response.setStatus(404);
    }
  }

  @RequestMapping("/preview/{folder}/{fileName:.+}")
  public void getStaticContent(@PathVariable String folder,
                               @PathVariable String fileName,
                               HttpServletResponse response) throws IOException {
    String baseDir = previewInfoDao.getBaseDir(folder);
    String filePath = baseDir + "/" + fileName;
    fileOutPutor.outPut(response, filePath, false);
  }
}
