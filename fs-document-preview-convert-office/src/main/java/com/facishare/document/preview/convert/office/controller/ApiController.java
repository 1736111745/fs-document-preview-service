package com.facishare.document.preview.convert.office.controller;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.convert.office.model.ConvertResultInfo;
import com.facishare.document.preview.convert.office.service.ConvertDocumentFormat;
import com.facishare.document.preview.convert.office.service.GetDocumentPageInfoService;
import com.facishare.document.preview.convert.office.service.GetUtilObject;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author liuquan
 * @date 2022/3/30  4:45 下午
 */
@Controller
@RequestMapping("/Api/Office/")
public class ApiController {

  @Resource
  private ConvertDocumentFormat convertDocumentFormat;

  @Resource
  private GetDocumentPageInfoService pageInfoService;

  @Resource
  private GetUtilObject getUtilObject;

  @Resource
  private ConvertResultInfo convertResultInfo;


  @ResponseBody
  @RequestMapping(value = "/GetPageInfoByStream", method = RequestMethod.POST)
  public String GetPageInfoByStream(String path, @RequestParam("file") MultipartFile file) {
    try {
      byte[] bytes = file.getBytes();
      if (getUtilObject.getParameterCalibration().isEmpty(path, bytes)) {
        return JSON.toJSONString(getUtilObject.getPageInfo().getFalsePageInfo("params is Empty"));
      }
      return JSON.toJSONString(pageInfoService.getPageInfo(bytes, path));
    } catch (IOException e) {
      return JSON.toJSONString(getUtilObject.getPageInfo().getFalsePageInfo(e.toString()));
    }
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertExcel2HtmlByStream", method = RequestMethod.POST)
  public String ConvertExcel2HtmlByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    try {
      byte[] bytes = file.getBytes();
      if (getUtilObject.getParameterCalibration().isEmpty(path, bytes)) {
        response = getUtilObject.getResponse().getFalseResponse(response);
        return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult("The file name or file data is empty"));
      } else {
        convertResultInfo = convertDocumentFormat.convertOnePageExcelToHtml(bytes, page);
        if (convertResultInfo.isSuccess()) {
          response = getUtilObject.getResponse().getTrueResponse(response);
          IOUtils.copy(new ByteArrayInputStream(convertResultInfo.getBytes()), response.getOutputStream());
          return null;
        }
      }
    } catch (IOException e) {
      response = getUtilObject.getResponse().getFalseResponse(response);
      return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult(e.toString()));
    }
    response = getUtilObject.getResponse().getFalseResponse(response);
    return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult(convertResultInfo.getErrorMsg()));
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertOffice2PdfByStream", method = RequestMethod.POST)
  public String ConvertOffice2PdfByStream(String path, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    try {
      byte[] bytes = file.getBytes();
      if (getUtilObject.getParameterCalibration().isEmpty(path, bytes)) {
        response = getUtilObject.getResponse().getFalseResponse(response);
        return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult("Office2Pdf,The file name or file data is empty"));
      } else {
        convertResultInfo = convertDocumentFormat.convertAllPageWordOrPptToPdf(bytes, path);
        if (convertResultInfo.isSuccess()) {
          response = getUtilObject.getResponse().getTrueResponse(response);
          IOUtils.copy(new ByteArrayInputStream(convertResultInfo.getBytes()), response.getOutputStream());
          return null;
        }
      }
    } catch (IOException e) {
      response = getUtilObject.getResponse().getFalseResponse(response);
      return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult(e.toString()));
    }
    response = getUtilObject.getResponse().getFalseResponse(response);
    return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult(convertResultInfo.getErrorMsg()));
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertFileByStream", method = RequestMethod.POST)
  public String ConvertFileByStream(String path, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    try {
      byte[] bytes = file.getBytes();
      if (getUtilObject.getParameterCalibration().isEmpty(path, bytes)) {
        response = getUtilObject.getResponse().getFalseResponse(response);
        return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult("The file name or file data is empty"));
      } else {
        convertResultInfo = convertDocumentFormat.convertDocumentSuffix(bytes, path);
        if (convertResultInfo.isSuccess()) {
          response = getUtilObject.getResponse().getTrueResponse(response);
          IOUtils.copy(new ByteArrayInputStream(convertResultInfo.getBytes()), response.getOutputStream());
          return null;
        }
      }
    } catch (IOException e) {
      response = getUtilObject.getResponse().getFalseResponse(response);
      return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult(e.toString()));
    }
    response = getUtilObject.getResponse().getFalseResponse(response);
    return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult(convertResultInfo.getErrorMsg()));
  }


  @ResponseBody
  @RequestMapping(value = "/ConvertOffice2PngByStream", method = RequestMethod.POST)
  public String ConvertOffice2PngByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    try {
      byte[] bytes = file.getBytes();
      if (getUtilObject.getParameterCalibration().isEmpty(path, bytes)) {
        response = getUtilObject.getResponse().getFalseResponse(response);
        return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult("The file name or file data is empty"));
      } else {
        convertResultInfo = convertDocumentFormat.convertDocumentAllPageToPng(bytes, path);
        if (convertResultInfo.isSuccess()) {
          response = getUtilObject.getResponse().getTrueResponse(response);
          IOUtils.copy(new ByteArrayInputStream(convertResultInfo.getBytes()), response.getOutputStream());
          return null;
        }
      }
    } catch (IOException e) {
      response = getUtilObject.getResponse().getFalseResponse(response);
      return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult(e.toString()));
    }
    response = getUtilObject.getResponse().getFalseResponse(response);
    return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult(convertResultInfo.getErrorMsg()));
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertOnePageOffice2PdfByStream", method = RequestMethod.POST)
  public String ConvertOnePageOffice2PdfByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    try {
      byte[] bytes = file.getBytes();
      if (getUtilObject.getParameterCalibration().isEmpty(path, bytes)) {
        response = getUtilObject.getResponse().getFalseResponse(response);
        return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult("The file name or file data is empty"));
      } else {
        convertResultInfo = convertDocumentFormat.convertDocumentOnePageToPng(bytes, path, page);
        if (convertResultInfo.isSuccess()) {
          response = getUtilObject.getResponse().getTrueResponse(response);
          IOUtils.copy(new ByteArrayInputStream(convertResultInfo.getBytes()), response.getOutputStream());
          return null;
        }
      }
    } catch (IOException e) {
      response = getUtilObject.getResponse().getFalseResponse(response);
      return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult(e.toString()));
    }
    response = getUtilObject.getResponse().getFalseResponse(response);
    return JSON.toJSONString(getUtilObject.getConvertResult().getFalseConvertResult(convertResultInfo.getErrorMsg()));
  }

}























