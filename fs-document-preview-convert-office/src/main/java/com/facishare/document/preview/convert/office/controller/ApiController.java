package com.facishare.document.preview.convert.office.controller;

import com.facishare.document.preview.common.model.ConvertResult;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.service.ConvertDocumentFormatService;
import com.facishare.document.preview.convert.office.service.DocumentPageInfoService;
import com.facishare.document.preview.convert.office.utils.PageInfoUtil;
import com.facishare.document.preview.convert.office.utils.ParameterCalibrationUtil;
import com.facishare.document.preview.convert.office.utils.ResponseUtil;
import java.io.ByteArrayOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public PageInfo getPageInfoByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file) {
    PageInfo pageInfo = documentPageInfoService.getPageInfo(file,
        ParameterCalibrationUtil.getFileType(path));
    if (pageInfo.getPageCount() == 0) {
      return PageInfoUtil.getPageInfo(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ZERO);
    }
    return pageInfo;
  }

  @PostMapping(value = "/ConvertExcel2HtmlByStream")
  public ConvertResult convertExcel2HtmlByStream(@RequestParam("path") String path,
      @RequestParam("page") int page, @RequestParam("file") MultipartFile file,
      HttpServletResponse response) throws IOException {
    ParameterCalibrationUtil.isExcelType(path);
    ResponseUtil.setResponse(response);
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      try (ByteArrayInputStream fileInputStream = new ByteArrayInputStream(
          convertDocumentFormatService.convertOnePageExcelToHtml(file, page - 1, fileOutputStream)
              .toByteArray())) {
        IOUtils.copy(fileInputStream, response.getOutputStream());
      }
    }
    return null;
  }

  @PostMapping(value = "/ConvertOffice2PdfByStream")
  public String convertOffice2PdfByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path);
    ResponseUtil.setResponse(response);
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      try (ByteArrayInputStream fileInputStream = new ByteArrayInputStream(
          convertDocumentFormatService.convertAllPageWordOrPptToPdf(file, fileType,
              fileOutputStream).toByteArray())) {
        IOUtils.copy(fileInputStream, response.getOutputStream());
      }
    }
    return null;
  }

  @PostMapping(value = "/ConvertFileByStream")
  public String convertFileByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path);
    ResponseUtil.setResponse(response);
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      try (ByteArrayInputStream fileInputStream = new ByteArrayInputStream(
          convertDocumentFormatService.convertDocumentSuffix(file, fileType, fileOutputStream)
              .toByteArray())) {
        IOUtils.copy(fileInputStream, response.getOutputStream());
      }
    }
    return null;
  }

  @PostMapping(value = "/ConvertOffice2PngByStream")
  public String convertOffice2PngByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path);
    ResponseUtil.setResponse(response);
    IOUtils.copy(new ByteArrayInputStream(
            convertDocumentFormatService.convertDocumentAllPageToPng(file, fileType)),
        response.getOutputStream());
    return null;
  }

  @PostMapping(value = "/ConvertOnePageOffice2PngByStream")
  public String convertOnePageOffice2PngByStream(@RequestParam("path") String path,
      @RequestParam("page") int page, @RequestParam("file") MultipartFile file,
      HttpServletResponse response) throws IOException {
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path);
    //要求页码不能为0开始，但word ppt  实际页码就是从0开始的，所以要减去1 而pdf是从1开始的，所以要在里面+1
    page = ParameterCalibrationUtil.isZero(page) - 1;
    ResponseUtil.setResponse(response);
    //转图片，所有文档类型下标都是从0开始
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      try (ByteArrayInputStream fileInputStream = new ByteArrayInputStream(
          convertDocumentFormatService.convertDocumentOnePageToPng(file, fileType, page,
              fileOutputStream).toByteArray())) {
        IOUtils.copy(fileInputStream, response.getOutputStream());
      }
    }
    return null;
  }

  @PostMapping(value = "/ConvertOnePageOffice2PdfByStream")
  public String convertOnePageOffice2PdfByStream(String path, int page,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path);
    // 这里只处理word和ppt文档 但ppt下标就是从1开始的而word的是从0开始的，所以不再这里对页码进行处理，而在服务层对调用word方法的page参数做减一操作
    ParameterCalibrationUtil.isZero(page);
    ResponseUtil.setResponse(response);
    //转pdf，只有Word文档下标从0开始
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      try (ByteArrayInputStream fileInputStream = new ByteArrayInputStream(
          convertDocumentFormatService.convertDocumentOnePageToPdf(file, fileType, page,
              fileOutputStream).toByteArray())) {
        IOUtils.copy(fileInputStream, response.getOutputStream());
      }
    }
    return null;
  }


}























