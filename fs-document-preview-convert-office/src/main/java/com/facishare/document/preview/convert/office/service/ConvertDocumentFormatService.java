package com.facishare.document.preview.convert.office.service;

import com.facishare.document.preview.convert.office.model.ConvertResultInfo;

/**
 * @author Andy
 */
public interface ConvertDocumentFormatService {
  /**
   * ConvertResultInfo 转换一页 Excel表为HTML格式
   *
   * @param data 要转换的Excel文件的字节流
   * @param page 要转换的页码
   * @return 返回转换为HTML格式的 单页文档字节流
   */
  public ConvertResultInfo convertOnePageExcelToHtml(byte[] data, int page);

  /**
   * ConvertResultInfo 将word文档或ppt文档 全部转换为PDF格式
   *
   * @param data     要转换的word文档或ppt文档的字节流
   * @param fileName 要转换的word文档或ppt文档的文件路径
   * @return 返回转换为PDF格式的文档的字节流
   */
  public ConvertResultInfo convertAllPageWordOrPptToPdf(byte[] data, String fileName);

  /**
   * ConvertResultInfo 转换文档格式 如 ppt转换pptx doc转换为docx xls转换为xlsx
   *
   * @param data     要转换的文档的字节流 （ppt、doc、xlsx)
   * @param fileName 要转化的文档的路径
   * @return 成功：返回已经转换好的文档的字节流 失败：返回报错信息（如文件损坏或文件加锁）
   */
  public ConvertResultInfo convertDocumentSuffix(byte[] data, String fileName);

  /**
   * ConvertResultInfo 将文档的全部页面转换为图片格式 并打成压缩包以字节流的形式返回
   *
   * @param data     要转换的文档的字节流（ppt、doc、xlsx)
   * @param fileName 要转化的文档的路径
   * @return 成功：返回转换为png格式并打成压缩包格式的字节流 失败：返回报错信息（如目标文件夹路径不存在，源文件不存在）
   */
  public ConvertResultInfo convertDocumentAllPageToPng(byte[] data, String fileName);

  /**
   * convertDocumentOnePageToPng 转换一页文档为png
   *
   * @param data     要转换的文档的字节流
   * @param fileName 要转换的文档的路径
   * @param page     要转换的文档的页码
   * @return 成功：返回转换为png格式的字节流 失败：返回报错信息（如 目标文件夹路径不存在，源文件不存在）
   */
  public ConvertResultInfo convertDocumentOnePageToPng(byte[] data, String fileName, int page);

  /**
   * convertDocumentOnePageToPdf 转换一页文档为Pdf
   * @param bytes 要转换的文档的字节流
   * @param path  要转换的文档的路径
   * @param page  要转换的文档的页码
   * @return 成功：返回转换为pdf格式的字节流 失败：返回报错信息（如 目标文件夹路径不存在，源文件不存在）
   */

  ConvertResultInfo convertDocumentOnePageToPdf(byte[] bytes, String path, int page);
}
