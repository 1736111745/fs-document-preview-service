package com.facishare.document.preview.convert.office.constant;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/4/21 10:59]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/21 10:59]
 * @updateRemark : [说明本次修改内容]
 */
public enum FileTypeEnum {

  DOC("doc"), DOCX("docx"), PPT("ppt"), PPTX("pptx"), XLS("xls"), XLSX("xlsx"), PDF("pdf");

  private final String fileTypeName;
  FileTypeEnum(String fileTypeName) {
    this.fileTypeName = fileTypeName;
  }
  public String getFileTypeName() {
    return fileTypeName;
  }
}
