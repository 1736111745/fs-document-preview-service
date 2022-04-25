package com.facishare.document.preview.convert.office.exception;

import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/4/22 18:52]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/22 18:52]
 * @updateRemark : [说明本次修改内容]
 */
@Getter
@Setter
public class AsposeInstantiationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * 错误信息
   */
  protected String errorCode;
  protected String errorMsg;

  public AsposeInstantiationException() {
    super();
  }

  public AsposeInstantiationException(ErrorInfoEnum errorInfoEnum) {
    super(errorInfoEnum.getErrCode());
    this.errorMsg = errorInfoEnum.getErrorMsg();
  }

  public AsposeInstantiationException(ErrorInfoEnum errorInfoEnum, Throwable cause) {
    super(errorInfoEnum.getErrCode(), cause);
    this.errorCode = errorInfoEnum.getErrCode();
    this.errorMsg = errorInfoEnum.getErrorMsg();
  }

  public AsposeInstantiationException(String errorMsg) {
    super(errorMsg);
    this.errorMsg = errorMsg;
  }

  public AsposeInstantiationException(String errorCode, String errorMsg) {
    super(errorCode);
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
  }

  public AsposeInstantiationException(String errorCode, String errorMsg, Throwable cause) {
    super(cause);
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
  }

  public AsposeInstantiationException(ErrorInfoEnum errorMsg, Exception cause) {
    super(cause);
    this.errorMsg = errorMsg.getErrorMsg();
  }

  public String getMessage() {
    return errorMsg;
  }

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }
}

