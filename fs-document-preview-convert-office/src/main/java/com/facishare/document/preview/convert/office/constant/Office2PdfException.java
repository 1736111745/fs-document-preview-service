package com.facishare.document.preview.convert.office.constant;

public class Office2PdfException extends RuntimeException{

  private final int errorCode;
  private final String errorMessage;
  private final String errorReason;

  public Office2PdfException(ErrorInfoEnum errorInfoEnum) {
    super(errorInfoEnum.getErrorMessage());
    this.errorCode = errorInfoEnum.getErrorCode();
    this.errorMessage = errorInfoEnum.getErrorMessage();
    this.errorReason = errorInfoEnum.getErrorReason();
  }

  public Office2PdfException(ErrorInfoEnum errorInfoEnum, int errorParameters, String errorReason) {
    super(errorInfoEnum.getErrorMessage());
    this.errorCode = errorInfoEnum.getErrorCode();
    this.errorReason = errorReason;
    this.errorMessage= errorInfoEnum.getErrorMessage() + " 错误的参数是：" + errorParameters;
  }

  public Office2PdfException(ErrorInfoEnum errorInfoEnum, Throwable cause) {
    super(errorInfoEnum.getErrorMessage(), cause);
    this.errorCode = errorInfoEnum.getErrorCode();
    this.errorMessage= errorInfoEnum.getErrorMessage();
    this.errorReason = errorInfoEnum.getErrorReason();
  }

  public Office2PdfException(ErrorInfoEnum errorInfoEnum, Exception cause) {
    super(errorInfoEnum.getErrorMessage(),cause);
    this.errorCode = errorInfoEnum.getErrorCode();
    this.errorMessage = errorInfoEnum.getErrorMessage();
    this.errorReason = errorInfoEnum.getErrorReason();
  }


  public int getErrorCode() {
    return errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
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
