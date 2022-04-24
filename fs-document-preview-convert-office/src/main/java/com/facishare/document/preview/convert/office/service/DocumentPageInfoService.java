package com.facishare.document.preview.convert.office.service;


import com.facishare.document.preview.common.model.PageInfo;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Andy
 * @date 2022年4月12日
 */

public interface DocumentPageInfoService {

  /**
   * getPageInfo 在这里根据文件名 选择调用某个文档的getPageInfo方法
   *
   * @param fileStream     要获取页面信息的文件
   * @return 返回页码信息对象PageInfo 包含boolean 类型的是否成功的信息，以及String类型的 errorMsg的错误信息，以及int类型的 PageCount 页码信息 ，成功返回页码 失败返回errorMsg
   */
  public PageInfo getPageInfo(String filePath,ByteArrayInputStream fileStream);

  /**
   * getWordPageInfo 获取Word文档的全部页码
   *
   * @param fileStream     要获取页面信息的word文件
   * @return 返回成功Word文档的页码信息 失败返回errorMsg 信息
   */
  PageInfo getWordPageInfo(ByteArrayInputStream fileStream);

  /**
   * getExcelPageInfo 获取Word文档的全部页码
   *
   * @param fileStream     要获取页面信息的Excel文件
   * @return 返回成功Excel文档的页码信息 以及List<String>类型的 表名及是否为活动表 失败返回errorMsg 信息
   */
  PageInfo getExcelPageInfo(ByteArrayInputStream fileStream);

  /**
   * getPptPageInfo 获取PPt文档的全部页码
   *
   * @param fileStream     要获取页面信息的PPT文件
   * @return 返回成功Word文档的页码信息 失败返回errorMsg 信息
   */
  PageInfo getPptPageInfo( ByteArrayInputStream fileStream);

  /**
   * getPdfPageInfo 获取PDF文档的全部页码
   *
   * @param fileStream     要获取页面信息的PDF文件
   * @return 返回成功PDF文档的页码信息 失败返回errorMsg 信息
   */
  PageInfo getPdfPageInfo(ByteArrayInputStream fileStream);
}
