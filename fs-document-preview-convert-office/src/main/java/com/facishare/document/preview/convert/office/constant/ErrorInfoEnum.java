package com.facishare.document.preview.convert.office.constant;

public enum ErrorInfoEnum {
  FILE_TYPES_DO_NOT_MATCH("400", "文件类型不是受支持的类型"), PAGE_NUMBER_PARAMETER_ZERO("400", "页码为零"), WORD_ENCRYPTION_ERROR("400",
    "Word 文档对象实例化失败，可能的错误文件加密了"), EXCEL_ENCRYPTION_ERROR("400", "Excel 文档对象实例化失败，可能的错误是文件损坏或加密"), PPT_ENCRYPTION_ERROR("400",
    "PPT 文档对象实例化失败，可能的错误是文件损坏或加密"), PDF_ENCRYPTION_ERROR("400", "PDF 文档对象实例化失败，可能的错误是文件损坏或加密"), WORD_INSTANTIATION_ERROR("400",
    "Word 文档对象实例化失败，可能的错误文件损坏了"), EXCEL_INSTANTIATION_ERROR("400", "Excel 文档对象实例化失败，可能的错误是文件损坏了"), PPT_INSTANTIATION_ERROR("400",
    "PPT 文档对象实例化失败，可能的错误是文件损坏了"), PDF_INSTANTIATION_ERROR("400", "PDF 文档对象实例化失败，可能的错误是文件损坏了"), ABNORMAL_FILE_SIGNATURE("500", "文件签名文件验证异常，可能的错误是流未正常的打开"),


  ;

  private final String errCode;
  private final String errorMsg;

  ErrorInfoEnum(String errCode, String errorMsg) {
    this.errCode = errCode;
    this.errorMsg = errorMsg;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public String getErrCode() {
    return errCode;
  }
}
