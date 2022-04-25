package com.facishare.document.preview.convert.office.utils;

import com.aspose.pdf.Document;
import com.aspose.pdf.License;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.AsposeInstantiationException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Andy
 */
@Slf4j
public class PdfObjectUtil {

  public static int getPageCount(ByteArrayInputStream fileStream) throws Exception {
    return getPageCount(getPdf(fileStream));
  }

  public static int getPageCount(Document pdf) throws Exception {
    if (pdf == null) {
      return 0;
    }
    int pageCount = pdf.getPages().size();
    pdf.close();
    return pageCount;
  }

  public static Document getPdf(ByteArrayInputStream fileStream) throws AsposeInstantiationException {
    getPdfLicense();
    Document pdf;
    try {
      pdf = new Document(fileStream);
    } catch (Exception e) {
      throw new AsposeInstantiationException(ErrorInfoEnum.PDF_ENCRYPTION_ERROR, e);
    }
    return pdf;
  }

  public static void getPdfLicense() throws AsposeInstantiationException {
    try (InputStream is = PdfObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new AsposeInstantiationException(ErrorInfoEnum.ABNORMAL_FILE_SIGNATURE, e);
    }
  }

}
