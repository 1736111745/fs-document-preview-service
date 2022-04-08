package com.facishare.document.preview.office2pdf.controller;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.office2pdf.model.ConverResultInfo;
import com.facishare.document.preview.office2pdf.util.ConvertHelper;
import com.facishare.document.preview.office2pdf.util.ConvertResultHelper;
import com.facishare.document.preview.office2pdf.util.OfficeConversion;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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

  @RequestMapping(value = "/GetPageInfoByStream", method = RequestMethod.POST)
  public PageInfo GetPageInfoByStream(String path, @RequestParam("file") MultipartFile file) throws Exception {
    PageInfo pageInfo = new PageInfo();
    byte[] bytes = file.getBytes();
    if (path.isEmpty() || bytes.length == 0) {
      String msg = "parms error!";

      pageInfo.setErrorMsg(msg);
      pageInfo.setSuccess(false);

    } else {
      pageInfo = ConvertHelper.GetDocPageInfo(bytes, path);
    }
    return pageInfo;
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertExcel2HtmlByStream", method = RequestMethod.POST)
  public String ConvertExcel2HtmlByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    byte[] bytes;

    try {
      // 取出数据
      bytes = file.getBytes();
    } catch (IOException e) {
      //获得失败的响应对象 状态码、媒体类型、编码格式
      response = ConvertResultHelper.getFalseResponse();
      // 获得失败的异常信息对象，并将异常信息对象转换为json格式
      return JSON.toJSONString(ConvertResultHelper.getFalseConverResult(e.toString()));
    }

    //入参检查 如果文件为空或文件字节流为空 返回json格式的异常信息
    if (path.isEmpty()) {
      //获得响应对象 状态码、媒体类型、编码格式
      response = ConvertResultHelper.getFalseResponse();
      // 获得异常信息对象并将异常信息对象转换为json格式
      return JSON.toJSONString(ConvertResultHelper.getFalseConverResult("文件名是空的！"));
    }

    ConverResultInfo converResultInfo = ConvertHelper.Excel2Html(bytes, page);
    //文件处理成功 返回byte[] 字节流
    if (converResultInfo.isSuccess()) {
      response = ConvertResultHelper.getTrueResponse();
      bytes = converResultInfo.getBytes();
      try {
        IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
      } catch (IOException e) {
        return JSON.toJSONString(ConvertResultHelper.getFalseConverResult(e.toString()));
      }
      return null;
    }
    response = ConvertResultHelper.getFalseResponse();
    return JSON.toJSONString(ConvertResultHelper.getFalseConverResult(converResultInfo.getErrorMsg()));
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertOffice2PdfByStream", method = RequestMethod.POST)
  public String ConvertOffice2PdfByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {

    byte[] bytes;
    try {
      bytes = file.getBytes();
    } catch (IOException e) {
      //获得失败的响应对象 状态码、媒体类型、编码格式
      response = ConvertResultHelper.getFalseResponse();
      // 获得失败的异常信息对象，并将异常信息对象转换为json格式
      return JSON.toJSONString(ConvertResultHelper.getFalseConverResult(e.toString()));
    }

    //入参检查 如果文件为空或文件字节流为空 返回json格式的异常信息
    if (path.isEmpty()) {
      //获得失败的响应对象 状态码、媒体类型、编码格式
      response = ConvertResultHelper.getFalseResponse();
      // 获得失败的异常信息对象，并将异常信息对象转换为json格式
      return JSON.toJSONString(ConvertResultHelper.getFalseConverResult("Office2Pdf,文件是空的!"));
    }

    //文件名后缀排查 如果不是指定格式，返回JSON格式的错误信息
    ConverResultInfo converResultInfo;
    String fileSuffix = path.substring(path.lastIndexOf(".")).toLowerCase();
    switch (fileSuffix) {
      case ".doc":
      case ".docx":
        converResultInfo = ConvertHelper.Word2Pdf(bytes);
        break;
      case ".ppt":
      case ".pptx":
        converResultInfo = ConvertHelper.Ppt2Pdf(bytes);
        break;
      default: {
        //获得失败的响应对象 状态码、媒体类型、编码格式
        response = ConvertResultHelper.getFalseResponse();
        // 获得失败的异常信息对象，并将异常信息对象转换为json格式
        return JSON.toJSONString(ConvertResultHelper.getFalseConverResult("参数不是doc、docx、ppt、pptx的任意一种!" + fileSuffix));
      }
    }
    if (converResultInfo.isSuccess()) {
      bytes = converResultInfo.getBytes();
      try {
        IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
      } catch (IOException e) {
        //获得失败的响应对象 状态码、媒体类型、编码格式
        response = ConvertResultHelper.getFalseResponse();
        // 获得失败的异常信息对象，并将异常信息对象转换为json格式
        return JSON.toJSONString(ConvertResultHelper.getFalseConverResult(e.toString()));
      }
      response = ConvertResultHelper.getTrueResponse();
      return null;
    }
    //获得失败的响应对象 状态码、媒体类型、编码格式
    response = ConvertResultHelper.getFalseResponse();
    // 获得 文件处理失败 的异常信息对象，并将异常信息对象转换为json格式
    return JSON.toJSONString(ConvertResultHelper.getFalseConverResult(converResultInfo.getErrorMsg()));
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertFileByStream", method = RequestMethod.POST)
  public String ConvertFileByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {

    byte[] bytes;
    try {
      bytes = file.getBytes();
    } catch (IOException e) {
      response = ConvertResultHelper.getFalseResponse();
      return JSON.toJSONString(ConvertResultHelper.getFalseConverResult(e.toString()));
    }

    //入参检查 如果文件为空或文件字节流为空 返回json格式的异常信息
    if (path.isEmpty()) {
      response = ConvertResultHelper.getFalseResponse();
      return JSON.toJSONString(ConvertResultHelper.getFalseConverResult("Office2Pdf,文件是空的!"));
    }
    bytes = OfficeConversion.DoConvertWithAspose(bytes, path);
    try {
      // 将处理完成的 字节流写入response
      IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
    } catch (IOException e) {
      response = ConvertResultHelper.getFalseResponse();
      //将异常信息对象转换为json格式
      return JSON.toJSONString(ConvertResultHelper.getFalseConverResult("参数不是doc、docx、ppt、pptx的任意一种"));
    }
    // 文件处理成功
    response=ConvertResultHelper.getTrueResponse();
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "/ConvertOffice2PngByStream", method = RequestMethod.POST)
  public String ConvertOffice2PngByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    byte[] bytes;
    try {
      bytes = file.getBytes();
    } catch (IOException e) {
      response = ConvertResultHelper.getFalseResponse();
      return JSON.toJSONString(ConvertResultHelper.getFalseConverResult(e.toString()));
    }
    if (path.isEmpty()) {
      response = ConvertResultHelper.getFalseResponse();
      return JSON.toJSONString(ConvertResultHelper.getFalseConverResult("Office2PngByStream 方法文件路径参数为空"));
    }
    ConverResultInfo converResultInfo;
    String fileSuffix = path.substring(path.lastIndexOf(".")).toLowerCase();
    switch (fileSuffix) {
      case ".doc":
      case ".docx":
       converResultInfo= ConvertHelper.Word2Png(bytes, page);
        break;
      case ".ppt":
      case ".pptx":
        converResultInfo=ConvertHelper.Ppt2Png(bytes, page);
        break;
      case ".pdf":
        converResultInfo=ConvertHelper.Pdf2Png(bytes, page);
        break;
      default: {
        response = ConvertResultHelper.getFalseResponse();
        return JSON.toJSONString(ConvertResultHelper.getFalseConverResult("parems error,need doc or ppt or pdf,but now" + fileSuffix));
      }
    }
    response=ConvertResultHelper.getTrueResponse();
    return null;
  }

}
