package com.facishare.document.preview.convert.office.controller;

import com.facishare.document.preview.common.model.ConvertResult;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.facishare.document.preview.convert.office.model.PageInfo;
import com.facishare.document.preview.convert.office.service.ConvertDocumentSuffixService;
import com.facishare.document.preview.convert.office.service.ConvertExcelToHtmlFormatService;
import com.facishare.document.preview.convert.office.service.ConvertOfficeToPdfFormatService;
import com.facishare.document.preview.convert.office.service.ConvertOfficeToPngFormatService;
import com.facishare.document.preview.convert.office.service.DocumentPageInfoService;
import com.facishare.document.preview.convert.office.utils.ParameterCalibrationUtil;
import com.facishare.document.preview.convert.office.utils.ResponseUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Andy
 */
@RestController
@RequestMapping("/Api/Office/")
public class ApiController {


  @Resource
  private DocumentPageInfoService documentPageInfoService;

  @Resource
  private ConvertExcelToHtmlFormatService convertExcelToHtmlFormatService;

  @Resource
  private ConvertOfficeToPdfFormatService convertAllPageWordOrPptToPdf;

  @Resource
  private ConvertDocumentSuffixService convertDocumentSuffix;

  @Resource
  private ConvertOfficeToPngFormatService convertDocumentAllPageToPng;


  @PostMapping("/GetPageInfoByStream")
  public PageInfo getPageInfo(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) {
    byte[] fileBytes = ParameterCalibrationUtil.isNullOrEmpty(path, file);
    return documentPageInfoService.getPageInfo(fileBytes, ParameterCalibrationUtil.getFileType(path));
  }

  @PostMapping(value = "/ConvertExcel2HtmlByStream")
  public ConvertResult convertExcel2HtmlByStream(@RequestParam("path") String path, @RequestParam("page") int page, @RequestParam("file") MultipartFile file, HttpServletResponse response){
    ParameterCalibrationUtil.isExcelType(path);
    ResponseUtil.setResponse(response);
    try (InputStream inputStream=file.getInputStream()){
      try (OutputStream outputStream=response.getOutputStream()) {
        outputStream.write(convertExcelToHtmlFormatService.convertOnePageExcelToHtml(inputStream, page));
      } catch (IOException e) {
        throw new Office2PdfException(ErrorInfoEnum.RESPONSE_STREAM_ERROR, e);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_STREAM_ERROR, e);
    }
    return null;
  }

  @PostMapping(value = "/ConvertOffice2PdfByStream")
  public String convertOffice2PdfByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file, HttpServletResponse response){
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path);
    ResponseUtil.setResponse(response);
    try (InputStream fileInputStream=file.getInputStream()){
      try (OutputStream outputStream=response.getOutputStream()) {
        outputStream.write(convertAllPageWordOrPptToPdf.convertAllPageWordOrPptToPdf(fileInputStream, fileType));
      } catch (IOException e) {
        throw new Office2PdfException(ErrorInfoEnum.RESPONSE_STREAM_ERROR, e);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_STREAM_ERROR, e);
    }
    return null;
  }

  @PostMapping(value = "/ConvertFileByStream")
  public String convertFileByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file, HttpServletResponse response){
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path);
    ResponseUtil.setResponse(response);
    try (InputStream fileInputStream=file.getInputStream()){
      try (OutputStream outputStream=response.getOutputStream()) {
        outputStream.write(convertDocumentSuffix.convertDocumentSuffix(fileInputStream, fileType));
      } catch (IOException e) {
        throw new Office2PdfException(ErrorInfoEnum.RESPONSE_STREAM_ERROR, e);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_STREAM_ERROR, e);
    }
    return null;
  }

  @PostMapping(value = "/ConvertOffice2PngByStream")
  public String convertOffice2PngByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path);
    ResponseUtil.setResponse(response);
    try (InputStream fileInputStream=file.getInputStream()){
      try (OutputStream outputStream=response.getOutputStream()) {
        outputStream.write(convertDocumentAllPageToPng.convertDocumentAllPageToPng(fileInputStream, fileType));
      } catch (IOException e) {
        throw new Office2PdfException(ErrorInfoEnum.RESPONSE_STREAM_ERROR, e);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_STREAM_ERROR, e);
    }
    return null;
  }

  @PostMapping(value = "/ConvertOnePageOffice2PngByStream")
  public String convertOnePageOffice2PngByStream(@RequestParam("path") String path, @RequestParam("page") int page, @RequestParam("file") MultipartFile file, HttpServletResponse response){
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path);
    //要求页码不能为0开始，但word ppt  实际页码就是从0开始的，所以要减去1 而pdf是从1开始的，所以要在里面+1
    ResponseUtil.setResponse(response);
    //转图片，所有文档类型下标都是从0开始
    try (InputStream fileInputStream=file.getInputStream()){
      try (OutputStream outputStream=response.getOutputStream()) {
        outputStream.write(convertDocumentAllPageToPng.convertDocumentOnePageToPng(fileInputStream, fileType,page));
      } catch (IOException e) {
        throw new Office2PdfException(ErrorInfoEnum.RESPONSE_STREAM_ERROR, e);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_STREAM_ERROR, e);
    }
    return null;
  }

  @PostMapping(value = "/ConvertOnePageOffice2PdfByStream")
  public String convertOnePageOffice2PdfByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response){
    FileTypeEnum fileType = ParameterCalibrationUtil.getFileType(path);
    // 这里只处理word和ppt文档 但ppt下标就是从1开始的而word的是从0开始的，所以不再这里对页码进行处理，而在服务层对调用word方法的page参数做减一操作
    ParameterCalibrationUtil.isZero(page);
    ResponseUtil.setResponse(response);
    //转pdf，只有Word文档下标从0开始
    try (InputStream fileInputStream=file.getInputStream()){
      try (OutputStream outputStream=response.getOutputStream()) {
        outputStream.write(convertAllPageWordOrPptToPdf.convertDocumentOnePageToPdf(fileInputStream, fileType,page));
      } catch (IOException e) {
        throw new Office2PdfException(ErrorInfoEnum.RESPONSE_STREAM_ERROR, e);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_STREAM_ERROR, e);
    }
    return null;
  }


}























