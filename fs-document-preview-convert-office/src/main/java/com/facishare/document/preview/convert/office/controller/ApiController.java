package com.facishare.document.preview.convert.office.controller;

import com.facishare.document.preview.common.model.ConvertResult;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.exception.BizException;
import com.facishare.document.preview.convert.office.service.ConvertDocumentFormatService;
import com.facishare.document.preview.convert.office.service.DocumentPageInfoService;
import com.facishare.document.preview.convert.office.utils.ConvertResultUtil;
import com.facishare.document.preview.convert.office.utils.PageInfoUtil;
import com.facishare.document.preview.convert.office.utils.ParameterCalibrationUtil;
import com.facishare.document.preview.convert.office.utils.ResponseUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Andy
 */
@RestController
@RequestMapping("/Api/Office/")
public class ApiController {


  @Resource
  private DocumentPageInfoService documentPageInfoService;

  @Resource
  private ConvertDocumentFormatService convertDocumentFormatService;


  @PostMapping("/GetPageInfoByStream")
  public PageInfo GetPageInfoByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) throws Exception {
    /**
     * 使用 try-catch-resources 模式， jvm会自动关闭流，即使发生了异常
     */
    byte[] fileBate = ParameterCalibrationUtil.isNullOrEmpty(path, file);
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path, fileBate);
    PageInfo pageInfo = documentPageInfoService.getPageInfo(fileBate, fileType);
    if (pageInfo.getPageCount() == 0) {
      return PageInfoUtil.getPageInfo(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ZERO);
    }
    return pageInfo;
  }

  @PostMapping(value = "/ConvertExcel2HtmlByStream")
  public ConvertResult ConvertExcel2HtmlByStream(@RequestParam("path") String path, @RequestParam("page") int page, @RequestParam("file") MultipartFile file,
                                                 HttpServletResponse response) throws IOException {
    if (page == 0) {
      return ConvertResultUtil.getConvertResult(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ZERO);
    }
    byte[] fileBate = ParameterCalibrationUtil.isNullOrEmpty(path, file);
    ParameterCalibrationUtil.isDifference(path, fileBate);
    IOUtils.copy(new ByteArrayInputStream(convertDocumentFormatService.convertOnePageExcelToHtml(fileBate, page - 1)), response.getOutputStream());
    ResponseUtil.getResponse(response);
    return null;
  }

  @PostMapping(value = "/ConvertOffice2PdfByStream")
  public String ConvertOffice2PdfByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file,
                                          HttpServletResponse response) throws IOException {
    byte[] fileBate = ParameterCalibrationUtil.isNullOrEmpty(path, file);
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path, fileBate);
    IOUtils.copy(new ByteArrayInputStream(convertDocumentFormatService.convertAllPageWordOrPptToPdf(fileBate, fileType)), response.getOutputStream());
    ResponseUtil.getResponse(response);
    return null;
  }

  @PostMapping(value = "/ConvertFileByStream")
  public String ConvertFileByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file,
                                    HttpServletResponse response) throws IOException {
    byte[] fileBate = ParameterCalibrationUtil.isNullOrEmpty(path, file);
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path, fileBate);
    IOUtils.copy(new ByteArrayInputStream(convertDocumentFormatService.convertDocumentSuffix(fileBate, fileType)), response.getOutputStream());
    ResponseUtil.getResponse(response);
    return null;
  }

  @PostMapping(value = "/ConvertOffice2PngByStream")
  public String ConvertOffice2PngByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file,
                                          HttpServletResponse response) throws IOException {
    byte[] fileBate = ParameterCalibrationUtil.isNullOrEmpty(path, file);
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path, fileBate);
    IOUtils.copy(new ByteArrayInputStream(convertDocumentFormatService.convertDocumentAllPageToPng(fileBate, fileType)), response.getOutputStream());
    ResponseUtil.getResponse(response);
    return null;
  }

  @RequestMapping(value = "/ConvertOnePageOffice2PngByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOnePageOffice2PngByStream(@RequestParam("path") String path, @RequestParam("page") int page, @RequestParam("file") MultipartFile file,
                                                 HttpServletResponse response) {
    return null;
  }

  @RequestMapping(value = "/ConvertOnePageOffice2PdfByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOnePageOffice2PdfByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    return null;
  }

  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public String insert() {
    throw new BizException("-1", "用户姓名不能为空！");
  }

}























