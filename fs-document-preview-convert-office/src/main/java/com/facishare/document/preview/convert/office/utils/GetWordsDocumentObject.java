package com.facishare.document.preview.convert.office.utils;


import com.aspose.words.License;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author AnDy
 */
@Slf4j
public class GetWordsDocumentObject {

  public com.aspose.words.Document getWord(byte[] data) {
    getWordsLicense();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    com.aspose.words.Document doc = null;
    try {
      doc = new com.aspose.words.Document(fileInputStream);
    } catch (Exception e) {
      log.error("获得Word-Document对象异常：" + e);
    }
    return doc;
  }

  public void getWordsLicense() {
    try {
      InputStream is = GetWordsDocumentObject.class.getClassLoader().getResourceAsStream("license.xml");
      License license = new com.aspose.words.License();
      license.setLicense(is);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
