package com.facishare.document.preview.convert.office.utils;

import com.aspose.pdf.License;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Andy
 */
public class GetPdfDocumentObject {

  public com.aspose.pdf.Document getPdf(byte[] data) {
    getPdfLicense();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    com.aspose.pdf.Document pdf;
    pdf = new com.aspose.pdf.Document(fileInputStream);
    return pdf;
  }

  public void getPdfLicense() {
    try {
      InputStream is = GetPdfDocumentObject.class.getClassLoader().getResourceAsStream("license.xml");
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
