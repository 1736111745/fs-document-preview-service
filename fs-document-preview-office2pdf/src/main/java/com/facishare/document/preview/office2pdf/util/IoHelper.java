package com.facishare.document.preview.office2pdf.util;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/4/2 11:19]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/2 11:19]
 * @updateRemark : [说明本次修改内容]
 */
public class IoHelper {
  public static byte[] StreamToBytes(MultipartFile file) throws IOException {
    return file.getBytes();
  }
}
