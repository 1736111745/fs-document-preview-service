package com.facishare.document.preview.convert.office.constant;

public enum FileTypeEnum{

  DOC("doc"),
  DOCX("docx"),
  PPT("ppt"),
  PPTX("pptx"),
  XLS("xls"),
  XLSX("xlsx"),
  PDF("pdf"),
  ZIP("zip");
  private final String fileTypeName;

  FileTypeEnum(String fileTypeName) {
    this.fileTypeName = fileTypeName;
  }
}
