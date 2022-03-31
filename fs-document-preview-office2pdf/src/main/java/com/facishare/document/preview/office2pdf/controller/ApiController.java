package com.facishare.document.preview.office2pdf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author liuquan
 * @date 2022/3/30  4:45 下午
 */
@Controller
@RequestMapping("/Api/Office/")
public class ApiController {

  @RequestMapping(value = "/GetPageInfoByStream",method = RequestMethod.POST)
  public void GetPageInfoByStream(@RequestParam("file") MultipartFile file) throws IOException {
    System.out.println(file.getBytes().length);
  }
}
