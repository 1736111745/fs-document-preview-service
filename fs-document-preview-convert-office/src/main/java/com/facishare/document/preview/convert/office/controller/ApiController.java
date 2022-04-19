package com.facishare.document.preview.convert.office.controller;

import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Andy
 */
@Slf4j
@Controller
@RequestMapping("/Api/Office/")
public class ApiController {


  @ResponseBody
  @RequestMapping(value = "/GetPageInfoByStream", method = RequestMethod.POST, produces = "application/json")
  public String GetPageInfoByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file) {

    return null;
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertExcel2HtmlByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertExcel2HtmlByStream(@RequestParam("path") String path,
      @RequestParam("page") int page, @RequestParam("file") MultipartFile file,
      HttpServletResponse response) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertOffice2PdfByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOffice2PdfByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertFileByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertFileByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    return null;
  }


  @ResponseBody
  @RequestMapping(value = "/ConvertOffice2PngByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOffice2PngByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertOnePageOffice2PngByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOnePageOffice2PngByStream(@RequestParam("path") String path,
      @RequestParam("page") int page, @RequestParam("file") MultipartFile file,
      HttpServletResponse response) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertOnePageOffice2PdfByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOnePageOffice2PdfByStream(String path, int page,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    return null;
  }
}























