package com.facishare.document.preview.convert.office.utils;

import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import java.io.InputStream;

/**
 * @author Andy
 */
public class InitializeAsposePptUtil {

  /**
   * 防止反射攻击
   */

  private InitializeAsposePptUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  /*
    在类加载时 加载PPT签名文件
   */
  static {
    try (InputStream is = InitializeAsposePptUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new com.aspose.slides.License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PPT_ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  public static Presentation getPpt(InputStream file) throws Office2PdfException {
    try{
      return new Presentation(file);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PPT_INSTANTIATION_ERROR, e);
    }
  }

}
