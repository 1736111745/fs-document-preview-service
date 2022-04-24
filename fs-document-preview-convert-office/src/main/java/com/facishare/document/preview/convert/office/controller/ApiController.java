package com.facishare.document.preview.convert.office.controller;

import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.exception.BizException;
import com.facishare.document.preview.convert.office.service.DocumentPageInfoService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Andy
 */
@Slf4j
@RestController
@RequestMapping("/Api/Office/")
public class ApiController {


  @Resource
  private DocumentPageInfoService documentPageInfoService;

  @PostMapping("/GetPageInfoByStream")
  public PageInfo GetPageInfoByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) {
    /**
     * 使用 try-catch-resources 模式， jvm会自动关闭流，即使发生了异常
     */
    try(ByteArrayInputStream fileStream=new ByteArrayInputStream(file.getBytes())){
      return documentPageInfoService.getPageInfo(path,fileStream);
    } catch (IOException e) {
      log.info("GetPageInfoByStream Controller Failed to get the file byte stream:",e);
    }
    return null;
  }

  @RequestMapping(value = "/ConvertExcel2HtmlByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertExcel2HtmlByStream(@RequestParam("path") String path,
      @RequestParam("page") int page, @RequestParam("file") MultipartFile file,
      HttpServletResponse response) {
    return null;
  }

  @RequestMapping(value = "/ConvertOffice2PdfByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOffice2PdfByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    return null;
  }

  @RequestMapping(value = "/ConvertFileByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertFileByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    return null;
  }


  @RequestMapping(value = "/ConvertOffice2PngByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOffice2PngByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    return null;
  }

  @RequestMapping(value = "/ConvertOnePageOffice2PngByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOnePageOffice2PngByStream(@RequestParam("path") String path,
      @RequestParam("page") int page, @RequestParam("file") MultipartFile file,
      HttpServletResponse response) {
    return null;
  }

  @RequestMapping(value = "/ConvertOnePageOffice2PdfByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOnePageOffice2PdfByStream(String path, int page,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    return null;
  }

  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public String insert() {
    throw  new BizException("-1","用户姓名不能为空！");
  }

}























