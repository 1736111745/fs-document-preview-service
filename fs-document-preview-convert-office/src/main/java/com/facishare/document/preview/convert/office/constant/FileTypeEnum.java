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

  DOC("doc" ), DOCX("docx"),
  PPT("ppt"), PPTX("pptx"),
  XLS("xls"), XLSX("xlsx"),
  PDF("pdf"),ZIP("zip");

  private String fileTypeName;

  FileTypeEnum(String fileTypeName) {
    this.fileTypeName = fileTypeName;
  }

  public static String getFileTypeName(String fileTypeName){
    for (FileTypeEnum typeName:FileTypeEnum.values()){
      if (typeName.getFileTypeName()==fileTypeName){
        return typeName.fileTypeName;
      }
    }
    return null;
  }

  public String getFileTypeName() {
    return fileTypeName;
  }


}
