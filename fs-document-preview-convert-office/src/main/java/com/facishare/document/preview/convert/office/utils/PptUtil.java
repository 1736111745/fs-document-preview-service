package com.facishare.document.preview.convert.office.utils;


import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.PresentationFactory;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.Office2PdfException;
import com.facishare.document.preview.convert.office.domain.PageInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class PptUtil {

  /**
   * 防止反射攻击
   */
  private PptUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  /*
    在类加载时 加载PPT签名文件
   */
  static {
    try (InputStream is = PptUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PPT_ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  public static Presentation getPpt(byte[] fileBate) throws Office2PdfException {
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      return new Presentation(fileStream);
    } catch (Exception e) {
      if (isCheckEncrypt(fileBate)) {
        throw new Office2PdfException(ErrorInfoEnum.PPT_ENCRYPTION_ERROR, e);
      }
      throw new Office2PdfException(ErrorInfoEnum.PPT_INSTANTIATION_ERROR, e);
    }
  }

  public static Presentation getPpt(InputStream file) throws Office2PdfException {
    try{
      return new Presentation(file);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PPT_INSTANTIATION_ERROR, e);
    }
  }

  public static boolean isCheckEncrypt(byte[] fileBate) {
    // 如果文件加密 返回true
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      return PresentationFactory.getInstance().getPresentationInfo(fileStream).isEncrypted();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PPT_INSTANTIATION_ERROR, e);
    }
  }

  public static PageInfo getPptPageCount(byte[] file) {
    Presentation ppt= PptUtil.getPpt(file);
    try {
      return PageInfo.ok(ppt.getSlides().size());
    } catch (Exception e){
      throw new Office2PdfException(ErrorInfoEnum.PPT_PAGE_NUMBER_PARAMETER_ZERO, e);
    }finally {
      ppt.dispose();
    }
  }

  public static byte[] convertPptToPptx(byte[] file) {
    Presentation ppt = PptUtil.getPpt(file);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ppt.save(outputStream, com.aspose.slides.SaveFormat.Pptx);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PPT_FILE_SAVING_FAILURE, e);
    } finally {
      ppt.dispose();
    }
  }

  public static byte[] convertPptOnePageToPdf(InputStream file, int page) {
    Presentation ppt = PptUtil.getPpt(file);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      int[] page2 = {page};
      ppt.save(outputStream, page2, com.aspose.slides.SaveFormat.Pdf);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    } finally {
      ppt.dispose();
    }
  }

  public static byte[] convertPptToPdf(InputStream file) {
    Presentation ppt = PptUtil.getPpt(file);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ppt.save(outputStream, com.aspose.slides.SaveFormat.Pdf);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PPT_FILE_SAVING_FAILURE, e);
    } finally {
      ppt.dispose();
    }
  }




}
