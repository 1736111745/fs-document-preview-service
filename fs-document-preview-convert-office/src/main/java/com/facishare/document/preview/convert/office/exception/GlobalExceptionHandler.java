package com.facishare.document.preview.convert.office.exception;

import com.facishare.document.preview.convert.office.exception.impl.CommonEnum;
import com.facishare.document.preview.convert.office.model.ResultBody;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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
   * 处理自定义的业务异常
   * @param req
   * @param e
   * @return
   */
  @ExceptionHandler(value = BizException.class)
  @ResponseBody
  public ResultBody bizExceptionHandler(HttpServletRequest req, BizException e){
    log.error("发生业务异常！原因是：{}",e.getErrorMsg());
    return ResultBody.error(e.getErrorCode(),e.getErrorMsg());
  }

  /**
   * 处理空指针的异常
   * @param req
   * @param e
   * @return
   */
  @ExceptionHandler(value =NullPointerException.class)
  @ResponseBody
  public ResultBody exceptionHandler(HttpServletRequest req, NullPointerException e){
    log.error("发生空指针异常！原因是:",e);
    return ResultBody.error(CommonEnum.BODY_NOT_MATCH);
  }


  /**
   * 处理其他异常
   * @param req
   * @param e
   * @return
   */
  @ExceptionHandler(value =Exception.class)
  @ResponseBody
  public ResultBody exceptionHandler(HttpServletRequest req, Exception e){
    log.error("未知异常！原因是:",e);
    return ResultBody.error(CommonEnum.INTERNAL_SERVER_ERROR);
  }
}


