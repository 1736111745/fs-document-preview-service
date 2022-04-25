package com.facishare.document.preview.convert.office.service;


import com.facishare.document.preview.common.model.PageInfo;

import java.io.ByteArrayInputStream;

/**
 * @author Andy
 * @date 2022年4月12日
 */

public interface DocumentPageInfoService {

  /**
   * getPageInfo 在这里根据文件名 选择调用某个文档的getPageInfo方法
   *
   * @param fileStream 要获取页面信息的文件
   * @return 返回页码信息对象PageInfo 包含boolean 类型的是否成功的信息，以及String类型的 errorMsg的错误信息，以及int类型的 PageCount 页码信息 ，成功返回页码 失败返回errorMsg
   */
  PageInfo getPageInfo(String filePath, ByteArrayInputStream fileStream) throws Exception;
}
