package com.facishare.document.preview.convert.office.utils;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.file.FileNameUtil;
import java.io.ByteArrayInputStream;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/4/21 10:30]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/21 10:30]
 * @updateRemark : [说明本次修改内容]
 */
public class OfficeFileTypeUtil {

  public static String getFileType(ByteArrayInputStream file) {
    return FileTypeUtil.getType(file);
  }
  public static String extName(String fileName){
    return FileNameUtil.extName(fileName);
  }

}
