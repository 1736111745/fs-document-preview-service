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
  private final int errorCode;
  private final String errorMessage;

  private final String errorReason;

  public Office2PdfException(ErrorInfoEnum errorInfoEnum) {
    super(errorInfoEnum.getErrorMessage());
    this.errorCode = errorInfoEnum.getErrCode();
    this.errorMessage = errorInfoEnum.getErrorMessage();
    this.errorReason = errorInfoEnum.getErrorReason();
  }

  public Office2PdfException(ErrorInfoEnum errorInfoEnum, int errorParameters, String errorReason) {
    super(errorInfoEnum.getErrorMessage());
    this.errorCode = errorInfoEnum.getErrCode();
    this.errorReason = errorReason;
    this.errorMessage = errorInfoEnum.getErrorMessage() + " 错误的参数是：" + errorParameters;
  }

  public Office2PdfException(ErrorInfoEnum errorInfoEnum, Throwable cause) {
    super(errorInfoEnum.getErrorMessage(),cause);
    this.errorCode = errorInfoEnum.getErrCode();
    this.errorMessage = errorInfoEnum.getErrorMessage();
    this.errorReason = errorInfoEnum.getErrorReason();
  }


  public Office2PdfException(ErrorInfoEnum errorInfoEnum, Exception cause) {
    super(cause);
    this.errorCode = errorInfoEnum.getErrCode();
    this.errorMessage = errorInfoEnum.getErrorMessage();
    this.errorReason = errorInfoEnum.getErrorReason();
  }

  @Override
  public String getMessage() {
    return errorMessage;
  }

  public int getErrorCode() {
    return errorCode;
  }
  public String getErrorReason() {
    return errorReason;
  }


  /*
   * 避免对api异常进行昂贵且无用的堆栈跟踪
   */
  @Override
  public Throwable fillInStackTrace() {
    return this;
  }
}

