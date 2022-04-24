package com.facishare.document.preview.convert.office.exception;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/4/22 18:52]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/22 18:52]
 * @updateRemark : [说明本次修改内容]
 */
public interface BaseErrorInfoInterface {

  /**
   * 错误码
   */
  String getResultCode();

  /**
   * 错误描述
   */
  String getResultMsg();
}
