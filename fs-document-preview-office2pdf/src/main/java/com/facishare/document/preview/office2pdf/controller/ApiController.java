package com.facishare.document.preview.office2pdf.controller;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.model.ConvertResult;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.office2pdf.util.ConvertHelper;
import com.facishare.document.preview.office2pdf.model.ConverResultInfo;
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
        //打印日志
//    FSTraceFinder.TraceMsgAsInfo("begin process request!");
        PageInfo pageInfo = new PageInfo();
        byte[] bytes = file.getBytes();
        if (path.isEmpty() || bytes.length == 0) {
            String msg = "parms error!";
            //打印日志
//      FSTraceFinder.TraceMsgAsError(msg);
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
        // 取出数据
        byte[] bytes = new byte[0];
        ConvertResult convertResult = new ConvertResult();
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            convertResult.setSuccess(false);
            convertResult.setErrorMsg(e.toString());
            response.setStatus(400);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //将异常信息对象转换为json格式
            return JSON.toJSONString(convertResult);
        }

        //入参检查 如果文件为空或文件字节流为空 返回json格式的异常信息
        if (path.isEmpty()) {
            // 设置异常信息对象
            convertResult.setSuccess(false);
            convertResult.setErrorMsg("文件名是空的!");
            //设置响应对象 状态码、媒体类型、编码格式
            response.setStatus(400);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //将异常信息对象转换为json格式
            return JSON.toJSONString(convertResult);
        }
        ConverResultInfo converResultInfo = ConvertHelper.Excel2Html(bytes, page);
        //文件处理成功 返回byte[] 字节流
        if (converResultInfo.isSuccess()) {
            response.setStatus(200);
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment");
            bytes = converResultInfo.getBytes();
            IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
            return null;
        }
        // 文件处理过程中出现异常，返回json格式的异常信息
        convertResult.setErrorMsg(converResultInfo.getErrorMsg());
        return JSON.toJSONString(convertResult);
    }

    @ResponseBody
    @RequestMapping(value = "/ConvertOffice2PdfByStream", method = RequestMethod.POST)
    public String ConvertOffice2PdfByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
        byte[] bytes;
        ConvertResult convertResult = new ConvertResult();
        ConverResultInfo converResultInfo;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            convertResult.setSuccess(false);
            convertResult.setErrorMsg(e.toString());
            response.setStatus(400);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //将异常信息对象转换为json格式
            return JSON.toJSONString(convertResult);
        }

        //入参检查 如果文件为空或文件字节流为空 返回json格式的异常信息
        if (path.isEmpty()) {
            convertResult.setSuccess(false);
            convertResult.setErrorMsg("Office2Pdf,文件是空的!");
            //设置响应对象 状态码、媒体类型、编码格式
            response.setStatus(400);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //将异常信息对象转换为json格式
            return JSON.toJSONString(convertResult);
        }
        //文件名后缀排查 如果不是指定格式，返回JSON格式的错误信息
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
                convertResult.setSuccess(false);
                convertResult.setErrorMsg("参数不是doc、docx、ppt、pptx的任意一种" + fileSuffix);
                //设置响应对象 状态码、媒体类型、编码格式
                response.setStatus(400);
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                //将异常信息对象转换为json格式
                return JSON.toJSONString(convertResult);
            }
        }
        if (converResultInfo.isSuccess()) {
            bytes = converResultInfo.getBytes();
            try {
                IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
            } catch (IOException e) {
                convertResult.setSuccess(false);
                convertResult.setErrorMsg("参数不是doc、docx、ppt、pptx的任意一种" + fileSuffix);
                response.setStatus(400);
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                //将异常信息对象转换为json格式
                return JSON.toJSONString(convertResult);
            }
            return null;
        }
        convertResult.setErrorMsg(converResultInfo.getErrorMsg());
        return JSON.toJSONString(convertResult);
    }

    @ResponseBody
    @RequestMapping(value = "/ConvertFileByStream", method = RequestMethod.POST)
    public String ConvertFileByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
        byte[] bytes;
        ConvertResult convertResult = new ConvertResult();
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            convertResult.setSuccess(false);
            convertResult.setErrorMsg(e.toString());
            response.setStatus(400);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //将异常信息对象转换为json格式
            return JSON.toJSONString(convertResult);
        }

        //入参检查 如果文件为空或文件字节流为空 返回json格式的异常信息
        if (path.isEmpty()) {
            convertResult.setSuccess(false);
            convertResult.setErrorMsg("Office2Pdf,文件是空的!");
            //设置响应对象 状态码、媒体类型、编码格式
            response.setStatus(400);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //将异常信息对象转换为json格式
            return JSON.toJSONString(convertResult);
        }
        bytes = OfficeConversion.DoConvertWithAspose(bytes, path);
        try {
            IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
        } catch (IOException e) {
            convertResult.setSuccess(false);
            convertResult.setErrorMsg("参数不是doc、docx、ppt、pptx的任意一种");
            response.setStatus(400);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //将异常信息对象转换为json格式
            return JSON.toJSONString(convertResult);
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/ConvertOffice2PngByStream", method = RequestMethod.POST)
    public String ConvertOffice2PngByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response) {
        byte[] bytes;
        ConvertResult convertResult = new ConvertResult();
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            convertResult.setSuccess(false);
            convertResult.setErrorMsg(e.toString());
            response.setStatus(400);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //将异常信息对象转换为json格式
            return JSON.toJSONString(convertResult);
        }
        if (path.isEmpty()) {
            convertResult.setSuccess(false);
            convertResult.setErrorMsg("fileName error!");
            response.setStatus(400);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //将异常信息对象转换为json格式
            return JSON.toJSONString(convertResult);
        }
        String fileSuffix = path.substring(path.lastIndexOf(".")).toLowerCase();
        switch (fileSuffix) {
            case ".doc":
            case ".docx":
                ConvertHelper.Word2Png(bytes, page);
                break;
            case ".ppt":
            case ".pptx":
                ConvertHelper.Ppt2Png(bytes, page);
                break;
            case ".pdf":
                ConvertHelper.Pdf2Png(bytes, page);
                break;
            default: {
                convertResult.setSuccess(false);
                convertResult.setErrorMsg("parems error,need doc or ppt or pdf,but now" + fileSuffix);
                response.setStatus(400);
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                //将异常信息对象转换为json格式
                return JSON.toJSONString(convertResult);
            }
        }


        return null;
    }

}
