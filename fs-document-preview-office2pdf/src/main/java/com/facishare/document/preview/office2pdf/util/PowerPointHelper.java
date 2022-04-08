package com.facishare.document.preview.office2pdf.util;

import com.aspose.slides.Presentation;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/4/1 13:36]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/1 13:36]
 * @updateRemark : [说明本次修改内容]
 */
public class PowerPointHelper {

  public static int GetPptxPageCount(byte[] data) throws IOException {
    int pageCount=0;
    ByteArrayInputStream fileInputStream=new ByteArrayInputStream(data);
    Presentation presentation=new com.aspose.slides.Presentation(fileInputStream);
    if(presentation!=null){
        //获取幻灯片的总页数
        pageCount=presentation.getSlides().size();
    }else {
      fileInputStream.close();
      return pageCount;
    }
    fileInputStream.close();
    return pageCount;
  }

}
