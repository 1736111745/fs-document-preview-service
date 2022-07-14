package com.facishare.document.preview.convert.office.utils;

import com.aspose.pdf.Document;
import com.aspose.pdf.License;
import com.aspose.pdf.facades.PdfFileInfo;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.Office2PdfException;
import com.facishare.document.preview.convert.office.domain.PageInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfUtil {

  /**
   * 防止反射攻击
   */

  private PdfUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  /*
    在类加载时 加载PDF签名文件
   */

  static {
    try (InputStream is = PdfUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PDF_ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  public static Document getPdf(byte[] fileBate) throws Office2PdfException {
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      return new Document(fileStream);
    } catch (Exception e) {
      if (isCheckEncrypt(fileBate)) {
        throw new Office2PdfException(ErrorInfoEnum.PDF_ENCRYPTION_ERROR, e);
      }
      throw new Office2PdfException(ErrorInfoEnum.PDF_INSTANTIATION_ERROR, e);
    }
  }

  public static boolean isCheckEncrypt(byte[] file) {
    try(ByteArrayInputStream fileStream= new ByteArrayInputStream(file)){
      try(PdfFileInfo pdfFileInfo = new PdfFileInfo(fileStream)) {
        return pdfFileInfo.isEncrypted();
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.PDF_INSTANTIATION_ERROR, e);
    }
  }

  public static PageInfo getPdfPageCount(byte[] file) {
    Document pdf = PdfUtil.getPdf(file);
    try{
      return PageInfo.ok(pdf.getPages().size());
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PDF_PAGE_NUMBER_PARAMETER_ZERO, e);
    }finally {
      pdf.close();
    }
  }
}
