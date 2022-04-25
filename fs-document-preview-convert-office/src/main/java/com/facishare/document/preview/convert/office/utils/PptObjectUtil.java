package com.facishare.document.preview.convert.office.utils;

import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.PresentationFactory;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.AsposeInstantiationException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Andy
 */
@Slf4j
public class PptObjectUtil {

  public static int getPageCount(ByteArrayInputStream fileStream) throws Exception {
    return getPageCount(getPpt(fileStream));
  }

  public static int getPageCount(Presentation ppt) throws Exception {
    if (ppt == null) {
      return 0;
    }
    int pageCount = ppt.getSlides().size();
    ppt.dispose();
    return pageCount;
  }

  public static Presentation getPpt(ByteArrayInputStream fileStream) throws AsposeInstantiationException {
    getPptLicense();
    Presentation ppt;
    try {
      ppt = new Presentation(fileStream);
    } catch (Exception e) {
      if (isCheckEncrypt(fileStream)) {
        throw new AsposeInstantiationException(ErrorInfoEnum.PPT_ENCRYPTION_ERROR, e);
      }
      throw new AsposeInstantiationException(ErrorInfoEnum.PPT_INSTANTIATION_ERROR, e);
    }
    return ppt;
  }

  public static void getPptLicense() throws AsposeInstantiationException {
    try (InputStream is = PptObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new AsposeInstantiationException(ErrorInfoEnum.ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  public static boolean isCheckEncrypt(ByteArrayInputStream fileStream) {
    // 如果文件加密 返回true
    return new PresentationFactory().getPresentationInfo(fileStream).isEncrypted();
  }


}
