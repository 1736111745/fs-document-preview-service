package com.facishare.document.preview.convert.office.utils;

import com.aspose.slides.License;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Andy
 */
public class GetPptDocumentObject {

  public com.aspose.slides.Presentation getPpt(byte[] data) {
    getPptLicense();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    com.aspose.slides.Presentation ppt=new com.aspose.slides.Presentation(fileInputStream);
    return ppt;
  }

  public void getPptLicense() {
    try {
      InputStream is = GetPptDocumentObject.class.getClassLoader().getResourceAsStream("license.xml");
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
