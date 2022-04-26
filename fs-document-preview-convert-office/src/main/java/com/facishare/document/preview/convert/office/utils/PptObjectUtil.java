package com.facishare.document.preview.convert.office.utils;

import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.PresentationFactory;
import com.aspose.slides.SaveFormat;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Andy
 */
@Slf4j
public class PptObjectUtil {

  public static int getPageCount(byte[] fileBate) throws Exception {
    return getPageCount(getPpt(fileBate));
  }

  public static int getPageCount(Presentation ppt) throws Exception {
    if (ppt == null) {
      return 0;
    }
    int pageCount = ppt.getSlides().size();
    ppt.dispose();
    return pageCount;
  }

  public static Presentation getPpt(byte[] fileBate) throws Office2PdfException {
    getPptLicense();
    Presentation ppt;
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      ppt = new Presentation(fileStream);
    } catch (Exception e) {
      if (isCheckEncrypt(fileBate)) {
        throw new Office2PdfException(ErrorInfoEnum.PPT_ENCRYPTION_ERROR, e);
      }
      throw new Office2PdfException(ErrorInfoEnum.PPT_INSTANTIATION_ERROR, e);
    }
    return ppt;
  }

  public static void getPptLicense() throws Office2PdfException {
    try (InputStream is = PptObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  public static boolean isCheckEncrypt(byte[] fileBate) {
    // 如果文件加密 返回true
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      return PresentationFactory.getInstance().getPresentationInfo(fileStream).isEncrypted();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.STREAM_CLOSING_ANOMALY, e);
    }
  }


  public static byte[] convertPptToPdf(byte[] fileBate) throws Office2PdfException {
    Presentation ppt = getPpt(fileBate);
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    ppt.save(fileOutputStream, SaveFormat.Pdf);
    ppt.dispose();
    fileBate = fileOutputStream.toByteArray();
    try {
      fileOutputStream.close();
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.STREAM_CLOSING_ANOMALY, e);
    }
    return fileBate;
  }

  public static byte[] convertPptToPptx(byte[] fileBate) {
    Presentation ppt = getPpt(fileBate);
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    try {
      ppt.save(fileOutputStream, SaveFormat.Pptx);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PPT_FILE_SAVING_FAILURE, e);
    }
    ppt.dispose();
    fileBate = fileOutputStream.toByteArray();
    try {
      fileOutputStream.close();
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.STREAM_CLOSING_ANOMALY, e);
    }
    return fileBate;
  }

}
