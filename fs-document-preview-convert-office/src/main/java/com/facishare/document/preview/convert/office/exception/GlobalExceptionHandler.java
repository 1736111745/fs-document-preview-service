package com.facishare.document.preview.convert.office.exception;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.model.ConvertResult;
import com.facishare.document.preview.convert.office.model.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/4/22 18:59]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/22 18:59]
 * @updateRemark : [说明本次修改内容]
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = Exception.class)
  public PageInfo exceptionHandler(HttpServletResponse response, Exception exception) {
    log.error("未知异常！原因是:", exception);
    response.setStatus(500);
    return  new PageInfo(exception.getMessage());
  }

  @ExceptionHandler(value = Office2PdfException.class)
  public String asposeInstantiationHandler(HttpServletResponse response, Office2PdfException e) {
    log.error("文档转换失败！原因是:", e);
    response.setStatus(Integer.parseInt(e.getErrorCode()));
    return JSON.toJSONString(new ConvertResult(e.getErrorMsg()));
  }

  @ExceptionHandler(value = IOException.class)
  public String asposeInstantiationHandler(HttpServletResponse response, IOException e) {
    log.error("response 输出流错误！原因是:", e);
    response.setStatus(500);
    return JSON.toJSONString(new ConvertResult(e.getMessage()));
  }

  @ExceptionHandler(value = MultipartException.class)
  public String asposeInstantiationHandler(HttpServletResponse response, MultipartException e) {
    log.error("请求错误！原因是:{}", e.getMessage());
    response.setStatus(400);
    return JSON.toJSONString(new ConvertResult(ErrorInfoEnum.FILE_PARAMETER_NULL.getErrorMsg()));
  }

}


