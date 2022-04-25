package com.facishare.document.preview.convert.office.exception;

import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.utils.PageInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 处理其他异常
   *
   * @param exception
   * @return
   */
  @ResponseBody
  @ExceptionHandler(value = Exception.class)
  public PageInfo exceptionHandler(HttpServletRequest request, Exception exception) {

    log.error("未知异常！原因是:", exception);
    return PageInfoUtil.getPageInfo(exception.getMessage());
  }

  @ResponseBody
  @ExceptionHandler(value = AsposeInstantiationException.class)
  public PageInfo AsposeInstantiationHandler(HttpServletRequest request, AsposeInstantiationException e) {
    log.error("Aspose对象实例化错误！原因是:", e);
    return PageInfoUtil.getPageInfo(e.getErrorMsg());
  }

}


