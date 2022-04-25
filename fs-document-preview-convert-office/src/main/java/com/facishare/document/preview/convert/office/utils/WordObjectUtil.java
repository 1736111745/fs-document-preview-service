package com.facishare.document.preview.convert.office.utils;


import com.aspose.words.Document;
import com.aspose.words.FileFormatUtil;
import com.aspose.words.License;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.AsposeInstantiationException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author AnDy
 */
public class WordObjectUtil {

  public static int getPageCount(ByteArrayInputStream fileStream) throws Exception {
    return getPageCount(getWord(fileStream));
  }

  public static int getPageCount(Document doc) throws Exception {
    if (doc == null) {
      return 0;
    }
    return doc.getPageCount();
  }

  public static Document getWord(ByteArrayInputStream fileStream) throws AsposeInstantiationException {
    getWordsLicense();
    Document document;
    try {
      document = new Document(fileStream);
    } catch (Exception e) {
      if (isCheckEncrypt(fileStream)) {
        throw new AsposeInstantiationException(ErrorInfoEnum.WORD_ENCRYPTION_ERROR, e);
      }
      throw new AsposeInstantiationException(ErrorInfoEnum.WORD_INSTANTIATION_ERROR, e);
    }
    return document;
  }

  public static void getWordsLicense() throws AsposeInstantiationException {
    try (InputStream is = WordObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new com.aspose.words.License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new AsposeInstantiationException(ErrorInfoEnum.ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  public static boolean isCheckEncrypt(ByteArrayInputStream fileStream) throws AsposeInstantiationException {
    // 如果文件加密 返回true
    try {
      return FileFormatUtil.detectFileFormat(fileStream).isEncrypted();
    } catch (Exception e) {
      throw new AsposeInstantiationException(ErrorInfoEnum.EXCEL_INSTANTIATION_ERROR, e);
    }
  }
}
