package com.facishare.document.preview.convert.office.utils;

import com.aspose.slides.IPresentationInfo;
import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.PresentationFactory;
import com.facishare.document.preview.convert.office.exception.BizException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Andy
 */
@Slf4j
public class PptObjectUtil {

  public com.aspose.slides.Presentation getPpt(ByteArrayInputStream fileStream) {
    getPptLicense();
    Presentation ppt = null;
    try {
      ppt = new Presentation(fileStream);
    } catch (Exception e) {
      //todo:处理异常，调用加密检查方法
      log.info("The document is encrypted and cannot be previewed");
      throw  new BizException("-1","The document is encrypted and cannot be previewed");
    }
    return ppt;
  }

  public void getPptLicense() {
    try(InputStream is = PptObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SneakyThrows
  public boolean isCheckEncrypt(ByteArrayInputStream fileStream){
    // 如果文件加密 返回true
    IPresentationInfo presentationInfo=new PresentationFactory().getPresentationInfo(fileStream);
    if (presentationInfo.isEncrypted())return true;
    int loadFormat=presentationInfo.getLoadFormat();
    //如果文件格式不为1-ppt 或 3-pptx 返回true
    return loadFormat != 255 && loadFormat != 3;
  }

  public int getPageCount(ByteArrayInputStream fileStream){
    int pageCount=getPageCount(getPpt(fileStream));
    return pageCount;
  }

  public int getPageCount(Presentation ppt){
    int pageCount=ppt.getSlides().size();
    ppt.dispose();
    return pageCount;
  }


}
