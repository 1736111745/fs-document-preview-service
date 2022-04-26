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
public class Office2PdfException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * 错误信息
   */
  protected String errorCode;
  protected String errorMsg;

  public Office2PdfException() {
    super();
  }

  public Office2PdfException(ErrorInfoEnum errorInfoEnum) {
    super(String.valueOf(errorInfoEnum.getErrCode()));
    this.errorMsg = errorInfoEnum.getErrorMsg();
  }

  public Office2PdfException(ErrorInfoEnum errorInfoEnum, Throwable cause) {
    super(String.valueOf(errorInfoEnum.getErrCode()), cause);
    this.errorCode = errorInfoEnum.getErrCode();
    this.errorMsg = errorInfoEnum.getErrorMsg();
  }

  public Office2PdfException(String errorMsg) {
    super(errorMsg);
    this.errorMsg = errorMsg;
  }

  public Office2PdfException(String errorCode, String errorMsg) {
    super(errorCode);
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
  }

  public Office2PdfException(String errorCode, String errorMsg, Throwable cause) {
    super(cause);
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
  }

  public Office2PdfException(ErrorInfoEnum errorInfoEnum, Exception cause) {
    super(cause);
    this.errorCode = errorInfoEnum.getErrCode();
    this.errorMsg = errorInfoEnum.getErrorMsg();
  }

  public String getMessage() {
    return errorMsg;
  }

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }
}

