package com.facishare.document.preview.convert.office.controller;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.convert.office.model.ConvertResultInfo;
import com.facishare.document.preview.convert.office.service.ConvertDocumentFormatService;
import com.facishare.document.preview.convert.office.service.GetDocumentPageInfoService;
import com.facishare.document.preview.convert.office.utils.GetConvertResult;
import com.facishare.document.preview.convert.office.utils.GetPageInfo;
import com.facishare.document.preview.convert.office.utils.GetResponse;
import com.facishare.document.preview.convert.office.utils.ParameterCalibration;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Controller
@RequestMapping("/Api/Office/")
public class ApiController {

  @Resource
  private ConvertDocumentFormatService convertDocumentFormatService;

  @Resource
  private GetDocumentPageInfoService pageInfoService;

  @ResponseBody
  @RequestMapping(value = "/GetPageInfoByStream", method = RequestMethod.POST,produces="application/json")
  public String GetPageInfoByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) {
    try {
      byte[] bytes = file.getBytes();
      if (ParameterCalibration.isEmpty(path, bytes)) {
        return JSON.toJSONString(GetPageInfo.getFalsePageInfo("params is Empty"));
      }
      return JSON.toJSONString(pageInfoService.getPageInfo(bytes, path));
    } catch (IOException e) {
      return JSON.toJSONString(GetPageInfo.getFalsePageInfo(e.toString()));
    }
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertExcel2HtmlByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertExcel2HtmlByStream(@RequestParam("path") String path, @RequestParam("page") int page, @RequestParam("file") MultipartFile file,
                                          HttpServletResponse response) {
    ConvertResultInfo convertResultInfo;
    try {
      byte[] bytes = file.getBytes();
      if (ParameterCalibration.isEmpty(path, bytes,page)) {
        response = GetResponse.getFalseResponse(response);
        return JSON.toJSONString(GetConvertResult.getFalseConvertResult("The file name or file data or filePage is empty"));
      } else {
        convertResultInfo = convertDocumentFormatService.convertOnePageExcelToHtml(bytes, page);
        if (convertResultInfo.isSuccess()) {
          response = GetResponse.getTrueResponse(response);
          IOUtils.copy(new ByteArrayInputStream(convertResultInfo.getBytes()), response.getOutputStream());
          return null;
        }
      }
    } catch (IOException e) {
      response = GetResponse.getFalseResponse(response);
      return JSON.toJSONString(GetConvertResult.getFalseConvertResult(e.toString()));
    }
    response = GetResponse.getFalseResponse(response);
    return JSON.toJSONString(GetConvertResult.getFalseConvertResult(convertResultInfo.getErrorMsg()));
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertOffice2PdfByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOffice2PdfByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    ConvertResultInfo convertResultInfo;
    try {
      byte[] bytes = file.getBytes();
      if (ParameterCalibration.isEmpty(path, bytes)) {
        response = GetResponse.getFalseResponse(response);
        return JSON.toJSONString(GetConvertResult.getFalseConvertResult("Office2Pdf,The file name or file data is empty"));
      } else {
        convertResultInfo = convertDocumentFormatService.convertAllPageWordOrPptToPdf(bytes, path);
        if (convertResultInfo.isSuccess()) {
          response = GetResponse.getTrueResponse(response);
          IOUtils.copy(new ByteArrayInputStream(convertResultInfo.getBytes()), response.getOutputStream());
          return null;
        }
      }
    } catch (IOException e) {
      response = GetResponse.getFalseResponse(response);
      return JSON.toJSONString(GetConvertResult.getFalseConvertResult(e.toString()));
    }
    response = GetResponse.getFalseResponse(response);
    return JSON.toJSONString(GetConvertResult.getFalseConvertResult(convertResultInfo.getErrorMsg()));
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertFileByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertFileByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    ConvertResultInfo convertResultInfo;
    try {
      byte[] bytes = file.getBytes();
      if (ParameterCalibration.isEmpty(path, bytes)) {
        response = GetResponse.getFalseResponse(response);
        return JSON.toJSONString(GetConvertResult.getFalseConvertResult("The file name or file data is empty"));
      } else {
        convertResultInfo = convertDocumentFormatService.convertDocumentSuffix(bytes, path);
        if (convertResultInfo.isSuccess()) {
          response = GetResponse.getTrueResponse(response);
          IOUtils.copy(new ByteArrayInputStream(convertResultInfo.getBytes()), response.getOutputStream());
          return null;
        }
      }
    } catch (IOException e) {
      response = GetResponse.getFalseResponse(response);
      return JSON.toJSONString(GetConvertResult.getFalseConvertResult(e.toString()));
    }
    response = GetResponse.getFalseResponse(response);
    return JSON.toJSONString(GetConvertResult.getFalseConvertResult(convertResultInfo.getErrorMsg()));
  }


  @ResponseBody
  @RequestMapping(value = "/ConvertOffice2PngByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOffice2PngByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    ConvertResultInfo convertResultInfo;
    try {
      byte[] bytes = file.getBytes();
      if (ParameterCalibration.isEmpty(path, bytes)) {
        response = GetResponse.getFalseResponse(response);
        return JSON.toJSONString(GetConvertResult.getFalseConvertResult("The file name or file data is empty"));
      } else {
        convertResultInfo = convertDocumentFormatService.convertDocumentAllPageToPng(bytes, path);
        if (convertResultInfo.isSuccess()) {
          response = GetResponse.getTrueResponse(response);
          IOUtils.copy(new ByteArrayInputStream(convertResultInfo.getBytes()), response.getOutputStream());
          return null;
        }
      }
    } catch (IOException e) {
      response = GetResponse.getFalseResponse(response);
      return JSON.toJSONString(GetConvertResult.getFalseConvertResult(e.toString()));
    }
    response = GetResponse.getFalseResponse(response);
    return JSON.toJSONString(GetConvertResult.getFalseConvertResult(convertResultInfo.getErrorMsg()));
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertOnePageOffice2PngByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOnePageOffice2PngByStream(@RequestParam("path") String path, @RequestParam("page") int page, @RequestParam("file") MultipartFile file,
                                                 HttpServletResponse response) {
    ConvertResultInfo convertResultInfo;
    try {
      byte[] bytes = file.getBytes();
      if (ParameterCalibration.isEmpty(path, bytes)) {
        response = GetResponse.getFalseResponse(response);
        return JSON.toJSONString(GetConvertResult.getFalseConvertResult("The file name or file data is empty"));
      } else {
        convertResultInfo = convertDocumentFormatService.convertDocumentOnePageToPng(bytes, path, page);
        if (convertResultInfo.isSuccess()) {
          response = GetResponse.getTrueResponse(response);
          IOUtils.copy(new ByteArrayInputStream(convertResultInfo.getBytes()), response.getOutputStream());
          return null;
        }
      }
    } catch (IOException e) {
      response = GetResponse.getFalseResponse(response);
      return JSON.toJSONString(GetConvertResult.getFalseConvertResult(e.toString()));
    }
    response = GetResponse.getFalseResponse(response);
    return JSON.toJSONString(GetConvertResult.getFalseConvertResult(convertResultInfo.getErrorMsg()));
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertOnePageOffice2PdfByStream", method = RequestMethod.POST, produces = "application/json")
  public String ConvertOnePageOffice2PdfByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    ConvertResultInfo convertResultInfo;
    try {
      byte[] bytes = file.getBytes();
      if (ParameterCalibration.isEmpty(path, bytes)) {
        response = GetResponse.getFalseResponse(response);
        return JSON.toJSONString(GetConvertResult.getFalseConvertResult("office2PngByStream 方法文件路径参数为空"));
      } else {
        convertResultInfo = convertDocumentFormatService.convertDocumentOnePageToPdf(bytes, path, page);
        if (convertResultInfo.isSuccess()) {
          response = GetResponse.getTrueResponse(response);
          IOUtils.copy(new ByteArrayInputStream(convertResultInfo.getBytes()), response.getOutputStream());
          return null;
        }
      }
    } catch (IOException e) {
      response = GetResponse.getFalseResponse(response);
      return JSON.toJSONString(GetConvertResult.getFalseConvertResult(e.toString()));
    }
    response = GetResponse.getFalseResponse(response);
    return JSON.toJSONString(GetConvertResult.getFalseConvertResult(convertResultInfo.getErrorMsg()));

  }
}























