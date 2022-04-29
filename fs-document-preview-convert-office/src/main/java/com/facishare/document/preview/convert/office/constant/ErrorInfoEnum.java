package com.facishare.document.preview.convert.office.constant;

public enum ErrorInfoEnum {
  FILE_TYPES_DO_NOT_MATCH("400", "文件类型不是受支持的类型"), FILE_PATH_EMPTY("400", "文件路径是空的"), EMPTY_FILE("400", "文件是空的"), FILE_PARAMETER_NULL("400",
    "文件参数是空的"), FILE_PARAMETER_ERROR("400", "没有获取到正确的文件，可能的错误是网络波动导致文件错误的传递"), PAGE_NUMBER_PARAMETER_ZERO("400", "文件损坏了，页码为0"), PAGE_NUMBER_PARAMETER_ERROR(
    "400",
    "页码错误 可能的错误是页码不在文档允许的范围内"), FILE_LINE_EMPTY_ERROR("400", "文件的有效行是空的"), WORD_ENCRYPTION_ERROR("400", "Word 文档对象实例化失败，文件加密了"), EXCEL_ENCRYPTION_ERROR("400",
    "Excel 文档对象实例化失败，可能的错误是文件加密了"), PPT_ENCRYPTION_ERROR("400", "PPT 文档对象实例化失败，可能的错误是文件加密了"), PDF_ENCRYPTION_ERROR("400",
    "PDF 文档对象实例化失败，可能的错误是文件加密了"), WORD_INSTANTIATION_ERROR("400", "Word 文档对象实例化失败，可能的错误文件损坏了"), EXCEL_INSTANTIATION_ERROR("400",
    "Excel 文档对象实例化失败，可能的错误是文件损坏了"), PPT_INSTANTIATION_ERROR("400", "PPT 文档对象实例化失败，可能的错误是文件损坏了"), PDF_INSTANTIATION_ERROR("400",
    "PDF 文档对象实例化失败，可能的错误是文件损坏了"), ABNORMAL_FILE_SIGNATURE("500", "文件签名文件验证异常，可能的错误是流未正常的打开"), STREAM_CLOSING_ANOMALY("500", "流关闭异常"), EXCEL_FILE_SAVING_FAILURE(
    "500",
    "EXCEL 文件转换失败 可能的错误是文件损坏了"), WORD_FILE_SAVING_FAILURE("500", "Word 文件转换失败 可能的错误是文件损坏了"), PPT_FILE_SAVING_FAILURE("500",
    "PPT 文件转换失败 可能的错误是文件损坏了"), PDF_FILE_SAVING_FAILURE("500", "PDF 文件转换失败 可能的错误是文件损坏了"), WORD_FILE_SAVING_PNG_FAILURE("500",
    "Word 文件转换图片失败 可能的错误是保存的目标并不存在"), PPT_FILE_SAVING_PNG_FAILURE("500", "PPT 文件转换图片失败 可能的错误是保存的目标并不存在"), PDF_FILE_SAVING_PNG_FAILURE("500",
    "PDF 文件转换图片失败 可能的错误是保存的目标并不存在"), UNABLE_CREATE_FOLDER("500", "不能在目标路径创建文件夹 可能的错误是目标路径并不存在"), FAILED_READ_DATA("500",
    "从目标文件路径读取数据失败 可能的错误是目标文件并不存在"), FILE_DELETION_FAILED("500", "删除目标文件夹失败 可能的错误是目标文件夹被其他程序占用"), INVALID_REFLECTION_ACCESS("500", "非法的通过反射访问对象"),

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
