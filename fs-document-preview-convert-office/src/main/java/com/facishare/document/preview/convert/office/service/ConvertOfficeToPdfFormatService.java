package com.facishare.document.preview.convert.office.service;

import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import java.io.InputStream;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/5/10 11:22]
 * @updateUser : [Andy]
 * @updateTime : [2022/5/10 11:22]
 * @updateRemark : [说明本次修改内容]
 */
public interface ConvertOfficeToPdfFormatService {
  /**
   * ConvertResultInfo 将word文档或ppt文档 全部转换为PDF格式
   *
   * @param file 要转换的word文档或ppt文档的字节流
   * @param fileType 文件类型枚举 DOC或DOCX
   * @return 返回转换为PDF格式的文档的字节流
   */
  byte[] convertAllPageWordOrPptToPdf(InputStream file, FileTypeEnum fileType);

  /**
   * convertDocumentOnePageToPdf 转换一页文档为Pdf
   *
   * @param file 要转换的文档的字节流
   * @param fileType 要转换的文档的后缀名
   * @param page     要转换的文档的页码
   * @return 成功：返回转换为pdf格式的字节流 失败：返回报错信息（如 目标文件夹路径不存在，源文件不存在）
   */

  byte[] convertDocumentOnePageToPdf(InputStream file, FileTypeEnum fileType, int page);
}
