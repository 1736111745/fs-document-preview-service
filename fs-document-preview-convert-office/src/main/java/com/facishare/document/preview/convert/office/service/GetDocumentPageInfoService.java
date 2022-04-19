package com.facishare.document.preview.convert.office.service;

import com.facishare.document.preview.convert.office.model.PageInfo;

/**
 * @author Andy
 * @date 2022年4月12日
 */

public interface GetDocumentPageInfoService {

  /**
   * getPageInfo 在这里根据文件名 选择调用某个文档的getPageInfo方法
   *
   * @param data     要获取的文件的字节流数据
   * @param filePath 要获取的文件的路径（包含文件名）
   * @return 返回页码信息对象PageInfo 包含boolean 类型的是否成功的信息，以及String类型的 errorMsg的错误信息，以及int类型的 PageCount 页码信息 ，成功返回页码 失败返回errorMsg
   */
  public PageInfo getPageInfo(byte[] data, String filePath);

  /**
   * getWordPageInfo 获取Word文档的全部页码
   *
   * @param data     要获取的Word文件的字节流数据
   * @param filePath 要获取的Word文件的路径（.doc 或 .docx）
   * @return 返回成功Word文档的页码信息 失败返回errorMsg 信息
   */
  PageInfo getWordPageInfo(byte[] data, String filePath);

  /**
   * getExcelPageInfo 获取Word文档的全部页码
   *
   * @param data     要获取的Excel文件的字节流数据
   * @param filePath 要获取的Excel文件的路径（.xls 或 .xlsx）
   * @return 返回成功Excel文档的页码信息 以及List<String>类型的 表名及是否为活动表 失败返回errorMsg 信息
   */
  PageInfo getExcelPageInfo(byte[] data, String filePath);

  /**
   * getPptPageInfo 获取PPt文档的全部页码
   *
   * @param data     要获取的PPT文件的字节流数据
   * @param filePath 要获取的PPt文件的路径（.ppt 或 .pptx）
   * @return 返回成功Word文档的页码信息 失败返回errorMsg 信息
   */
  PageInfo getPptPageInfo(byte[] data, String filePath);

  /**
   * getPdfPageInfo 获取PDF文档的全部页码
   *
   * @param data     要获取的PDF文件的字节流数据
   * @param filePath 要获取的PDF文件的路径（.pdf）
   * @return 返回成功PDF文档的页码信息 失败返回errorMsg 信息
   */
  PageInfo getPdfPageInfo(byte[] data, String filePath);
}
