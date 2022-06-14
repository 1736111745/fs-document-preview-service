package com.facishare.document.preview.convert.office.service;

import java.io.InputStream;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/5/10 11:21]
 * @updateUser : [Andy]
 * @updateTime : [2022/5/10 11:21]
 * @updateRemark : [说明本次修改内容]
 */
public interface ConvertExcelToHtmlFormatService {
  /**
   * ConvertResultInfo 转换一页 Excel表为HTML格式
   *
   * @param file 要转换的Excel文件的字节流
   * @param page     要转换的页码
   * @return 返回转换为HTML格式的 单页文档字节流
   */
  byte[] convertOnePageExcelToHtml(InputStream file, int page);
}
