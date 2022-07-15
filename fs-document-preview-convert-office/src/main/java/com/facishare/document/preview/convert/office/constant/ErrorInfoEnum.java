package com.facishare.document.preview.convert.office.constant;

public enum ErrorInfoEnum {

  IllegalArgumentException(400, "参数异常"),
  FILE_PATH_EMPTY(400, "空的文件路径", "ParameterCalibrationUtil工具类参数校验Path参数的值是空的"),
  EMPTY_FILE(400, "文件是空的", "ParameterCalibrationUtil工具类参数校验file参数的值小于等于50"),

  FILE_TYPES_DO_NOT_EXCEL(400, "当前预览模式仅支持Excel文件","在ExcelToHtml中,发送了其他的Office文档"),

  RESPONSE_STREAM_ERROR(500, "预览服务繁忙", "不能写入到response中流中，一般是Cgi挂掉了，要检查上级服务的状态"),

  FILE_STREAM_ERROR(400, "无法从文件参数中获取到文件数据", "ParameterCalibrationUtil工具类参数校验file参数, file.getBytes()不能拿到文件数据"),

  WORD_PAGE_NUMBER_PARAMETER_ZERO(400, "文件是空的，页码为0","Word文档实例化时，页码为0"),
  EXCEL_PAGE_NUMBER_PARAMETER_ZERO(400, "文件是空的，页码为0", "Excel文档实例化时，页码为0"),
  PPT_PAGE_NUMBER_PARAMETER_ZERO(400, "文件是空的，页码为0", "PPT文档实例化时，页码为0"),
  PDF_PAGE_NUMBER_PARAMETER_ZERO(400, "文件是空的，页码为0","PDF文档实例化时，页码为0"),
  FILE_TYPES_DO_NOT_MATCH(400,"暂不支持该文件类型的预览", "文件类型不属于Office文档类型,转换枚举类失败时候，抛出此异常"),
  WORD_ENCRYPTION_ERROR(400, "非常抱歉,暂不支持加密文件预览", "Word文档实例化时，文件是加密的"),
  PAGE_NUMBER_PARAMETER_ERROR(400, "预览页面超出了文档页面的可视范围", "页码错误 可能的错误是页码不在文档允许的范围内"),

  EXCEL_ENCRYPTION_ERROR(400, "非常抱歉,暂不支持加密文件预览", "Excel文档实例化时，文件是加密的"),
  PPT_ENCRYPTION_ERROR(400, "非常抱歉,暂不支持加密文件预览", "PPT文档实例化时，文件是加密的"),
  PDF_ENCRYPTION_ERROR(400, "非常抱歉,暂不支持加密文件预览", "PDF文档实例化时，文件是加密的"),
  WORD_PAGE_NUMBER_PARAMETER_ERROR(400, "页码参数错误,超出文档允许范围","Word文档转换为PDF文档时，页码参数错误，超出文档允许范围"),
  PPT_PAGE_NUMBER_PARAMETER_ERROR(400, "页码参数错误,超出文档允许范围","PPT文档转换为PDF文档时，页码参数错误，超出文档允许范围"),
  WORD_INSTANTIATION_ERROR(400, "文件损坏了，请重新上传", "word文档实例化时，文件损坏了，请重新上传"),
  EXCEL_INSTANTIATION_ERROR(400, "文件损坏了，请重新上传", "Excel文档实例化时，文件损坏了，请重新上传"),
  PPT_INSTANTIATION_ERROR(400, "文件损坏了，请重新上传","PPT文档实例化时，文件损坏了，请重新上传"),
  PDF_INSTANTIATION_ERROR(400, "文件损坏了，请重新上传","PDF文档实例化时，文件损坏了，请重新上传"),
  EXCEL_FILE_SAVING_FAILURE(500, "EXCEL 文件转换失败 可能的错误是文件损坏了", "Excel文档转换在转换为HTML时文件保存失败"),
  WORD_FILE_SAVING_FAILURE(500, "Word 文件转换失败 可能的错误是文件损坏了", "Word文档转换为PDF文档时，在嵌入字体阶段，文件保存失败"),
  PPT_FILE_SAVING_FAILURE(500, "PPT 文件转换失败 可能的错误是文件损坏了", "PPT文档转换为PDF文档时，文件保存失败"),
  WORD_ABNORMAL_FILE_SIGNATURE(500, "Word文件签名文件验证异常", "Word文件签名文件验证异常"),
  EXCEL_ABNORMAL_FILE_SIGNATURE(500, "Excel文件签名文件验证异常", "Excel文件签名文件验证异常"),
  PPT_ABNORMAL_FILE_SIGNATURE(500, "PPT文件签名文件验证异常", "PPT文件签名文件验证异常"),
  PDF_ABNORMAL_FILE_SIGNATURE(500, "PDF文件签名文件验证异常", "PDF文件签名文件验证异常"),
  FILE_PARAMETER_NULL(400, "文件是空的", "ParameterCalibrationUtil工具类参数校验file参数, file.getBytes()不能拿到文件数据"),
  UNABLE_CREATE_FOLDER(500, "预览服务繁忙", "不能在目标路径创建文件夹 可能的错误是目标路径并不存在"),
  FILE_DELETION_FAILED(500, "预览文件无法删除", "删除目标文件夹失败 可能的错误是目标文件夹被其他程序占用"),
  FAILED_READ_DATA(500, "无法读取压缩文件", "无法读取压缩文件, 可能的错误是目标文件夹被其他程序占用"),
  STREAM_CLOSING_ANOMALY(500, "预览文件太大啦,无法展示","PPT转换全部页面为PDF时，流关闭异常"),
  WORD_FILE_SAVING_PNG_FAILURE(500, "Word 文件图片预览模式失败","Word 文件转换图片失败 可能的错误是保存的目标并不存在(检查文件路径)"),
  PPT_FILE_SAVING_PNG_FAILURE(500, "PPT 文件图片预览模式失败", "PPT 文件转换图片失败 可能的错误是保存的目标并不存在(检查文件路径)"),
  PDF_FILE_SAVING_PNG_FAILURE(500, "PDF 文件图片预览模式失败","PDF 文件转换图片失败 可能的错误是保存的目标并不存在(检查文件路径)"),


  INVALID_REFLECTION_ACCESS(500, "非法访问", "非法的通过反射访问对象"),
  PDF_CLOSE_EXCEPTION(500,"PDF资源释放异常","PDF转图片资源释放异常");
  private final int errorCode;
  private final String errorMessage;
  private final String errorReason;

  ErrorInfoEnum(int errorCode, String errorMessage, String errorReason) {
    this.errorCode = errorCode;
    this.errorMessage =errorMessage;
    this.errorReason = errorReason;
  }

  ErrorInfoEnum(int errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage =errorMessage;
    this.errorReason = "未知错误";
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
}
