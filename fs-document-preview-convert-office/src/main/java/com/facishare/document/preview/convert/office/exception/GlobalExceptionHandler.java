package com.facishare.document.preview.convert.office.exception;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.convert.office.constant.Office2PdfException;
import com.facishare.document.preview.convert.office.domain.ConvertResult;
import com.facishare.document.preview.convert.office.domain.PageInfo;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = Exception.class)
  public PageInfo exceptionHandler(HttpServletResponse response, Exception exception) {
    log.error("服务端错误信息打印开始：未知异常！原因是:{}\n", exception.getMessage(), exception);
    response.setStatus(500);
    return  PageInfo.error(exception.getMessage());
  }

  @ExceptionHandler(value = Office2PdfException.class)
  public String asposeInstantiationHandler(HttpServletResponse response, Office2PdfException e) {
    if (e.getErrorCode()==400) {
      log.warn("客户端错误信息打印开始：\n错误码: {} ,\n错误信息: {} ,\n错误原因: {} .",e.getErrorCode(),e.getErrorMessage(),e.getErrorReason(),e);
    }
    if (e.getErrorCode()==500){
      log.error("服务端错误信息打印开始：\n错误码{},错误信息{},\n错误原因{}。",e.getErrorCode(),e.getErrorMessage(),e.getErrorReason());
    }
    response.setStatus(e.getErrorCode());
    return JSON.toJSONString(ConvertResult.error(e.getErrorMessage()));
  }

  @ExceptionHandler(value = IOException.class)
  public String asposeInstantiationHandler(HttpServletResponse response, IOException e) {
    log.error("服务端错误信息打印开始：response 输出流错误！原因是:{}\n", e.getMessage(),e);
    response.setStatus(500);
    return JSON.toJSONString(ConvertResult.error(e.getMessage()));
  }

  @ExceptionHandler(value = MultipartException.class)
  public String asposeInstantiationHandler(HttpServletResponse response, MultipartException e) {
    log.error("客户端错误信息打印开始：请求错误！原因是:{}\n", e.getMessage(),e);
    response.setStatus(400);
    return JSON.toJSONString(ConvertResult.error(e.getMessage()));
  }

}
