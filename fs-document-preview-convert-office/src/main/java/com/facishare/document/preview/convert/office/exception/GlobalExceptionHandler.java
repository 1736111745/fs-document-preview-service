package com.facishare.document.preview.convert.office.exception;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.utils.ConvertResultUtil;
import com.facishare.document.preview.convert.office.utils.PageInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    return PageInfoUtil.getPageInfo(exception.getMessage());
  }

  @ExceptionHandler(value = Office2PdfException.class)
  public String AsposeInstantiationHandler(HttpServletResponse response, Office2PdfException e) {
    log.error("文档转换失败！原因是:", e);
    response.setStatus(Integer.parseInt(e.errorCode));
    return JSON.toJSONString(ConvertResultUtil.getConvertResult(e.getErrorMsg()));
  }

  @ExceptionHandler(value = IOException.class)
  public String AsposeInstantiationHandler(HttpServletResponse response, IOException e) {
    log.error("response 输出流错误！原因是:", e);
    response.setStatus(500);
    return JSON.toJSONString(ConvertResultUtil.getConvertResult(e.getMessage()));
  }

}


