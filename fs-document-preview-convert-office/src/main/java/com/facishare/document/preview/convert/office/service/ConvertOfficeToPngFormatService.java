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
public interface ConvertOfficeToPngFormatService {
  /**
   * ConvertResultInfo 将文档的全部页面转换为图片格式 并打成压缩包以字节流的形式返回
   *
   * @param file 要转换的文档的字节流（ppt、doc、xlsx)
   * @param fileType 要转换的文档的后缀名
   * @return 成功：返回转换为png格式并打成压缩包格式的字节流 失败：返回报错信息（如目标文件夹路径不存在，源文件不存在）
   */
  byte[] convertDocumentAllPageToPng(InputStream file, FileTypeEnum fileType);

  /**
   * convertDocumentOnePageToPng 转换一页文档为png
   *
   * @param file 要转换的文档的字节流
   * @param fileType 要转换的文档的后缀名
   * @param page     要转换的文档的页码
   * @return 成功：返回转换为png格式的字节流 失败：返回报错信息（如 目标文件夹路径不存在，源文件不存在）
   */
  byte[] convertDocumentOnePageToPng(InputStream file, FileTypeEnum fileType, int page);
}
