package com.facishare.document.preview.convert.office.utils;

import com.aspose.pdf.Document;
import com.aspose.pdf.License;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Andy
 */
@Slf4j
public class PdfObjectUtil {

  public void getPdfLicense() {
    try (InputStream is = PdfObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public com.aspose.pdf.Document getPdf(ByteArrayInputStream fileStream) {
    getPdfLicense();
    Document pdf = null;
    try {
      pdf = new Document(fileStream);
    } catch (Exception e) {
      //todo:处理异常，调用加密检查方法
      log.info("The document is encrypted and cannot be previewed");
      e.printStackTrace();
    }
    return pdf;
  }

  @SneakyThrows
  public boolean isCheckEncrypt(Document pdf) {
    // 如果文件加密 返回true
    boolean encrypted = pdf.isEncrypted();
    int loadFormat = pdf.getPdfFormat();
    pdf.close();
    if (pdf.isEncrypted()) {
      return true;
    }
    //如果文件格式不为 12-pdf 返回true
    return loadFormat != 12;
  }

  public int getPageCount(Document pdf) {
    int pageCount = pdf.getPages().size();
    pdf.close();
    return pageCount;
  }
}
