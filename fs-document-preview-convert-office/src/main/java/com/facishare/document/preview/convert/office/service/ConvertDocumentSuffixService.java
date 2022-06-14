package com.facishare.document.preview.convert.office.service;

import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import java.io.InputStream;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/5/10 11:23]
 * @updateUser : [Andy]
 * @updateTime : [2022/5/10 11:23]
 * @updateRemark : [说明本次修改内容]
 */
public interface ConvertDocumentSuffixService {
  /**
   * ConvertResultInfo 转换文档格式 如 ppt转换pptx doc转换为docx xls转换为xlsx
   *
   * @param file 要转换的文档的字节流 （ppt、doc、xlsx)
   * @param fileType 要转换的文档的后缀名
   * @return 成功：返回已经转换好的文档的字节流 失败：返回报错信息（如文件损坏或文件加锁）
   */
  byte[] convertDocumentSuffix(InputStream file, FileTypeEnum fileType);
}
