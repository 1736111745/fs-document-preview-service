package com.facishare.document.preview.convert.office.utils;

import com.aspose.pdf.Document;
import com.aspose.pdf.License;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Andy
 */
@Slf4j
public class PdfObjectUtil {

  public static int getPageCount(byte[] fileBate) throws Exception {
    return getPageCount(getPdf(fileBate));
  }

  public static int getPageCount(Document pdf) throws Exception {
    if (pdf == null) {
      return 0;
    }
    int pageCount = pdf.getPages().size();
    pdf.close();
    return pageCount;
  }

  public static Document getPdf(byte[] fileBate) throws Office2PdfException {
    getPdfLicense();
    Document pdf;
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      pdf = new Document(fileStream);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PDF_ENCRYPTION_ERROR, e);
    }
    return pdf;
  }

  public static void getPdfLicense() throws Office2PdfException {
    try (InputStream is = PdfObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.ABNORMAL_FILE_SIGNATURE, e);
    }
  }

}
