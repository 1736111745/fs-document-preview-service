package com.facishare.document.preview.convert.office.utils;

import com.aspose.slides.ISlide;
import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.PresentationFactory;
import com.aspose.slides.SaveFormat;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Andy
 */
public class PptObjectUtil {

  private PptObjectUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  public static int getPageCount(byte[] fileBate) {
    return getPageCount(getPpt(fileBate));
  }

  public static int getPageCount(Presentation ppt) {
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
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      ppt.save(fileOutputStream, SaveFormat.Pptx);
      fileBate = fileOutputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PPT_FILE_SAVING_FAILURE, e);
    } finally {
      ppt.dispose();
    }
    return fileBate;
  }

  public static byte[] convertPptAllPageToPng(byte[] fileBate, String office2PngTempPath, String office2PngZipTempPath) {
    office2PngTempPath = FileProcessingUtil.createDirectory(office2PngTempPath);
    office2PngZipTempPath = FileProcessingUtil.createDirectory(office2PngZipTempPath);
    Presentation ppt = getPpt(fileBate);
    try {
      for (ISlide slide : ppt.getSlides()) {
        Dimension size = new Dimension(1280, 720);
        //设置生成图片的大小
        BufferedImage bufferedImage = slide.getThumbnail(size);
        File outputFile = new File(office2PngTempPath + "\\" + slide.getSlideNumber() + ".png");
        ImageIO.write(bufferedImage, "PNG", outputFile);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_PNG_FAILURE, e);
    } finally {
      ppt.dispose();
    }
    return FileProcessingUtil.getZipByte(office2PngTempPath, office2PngZipTempPath);
  }

  public static byte[] convertPptOnePageToPng(byte[] fileBate, int page) {
    Presentation ppt = getPpt(fileBate);
    //设置要转换的图片的格式
    Dimension size = new Dimension(1280, 720);
    //获得指定页码的幻灯片
    ISlide slide = ppt.getSlides().get_Item(page);
    //转换为图片流格式
    BufferedImage bufferedImage = slide.getThumbnail(size);
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      ImageIO.write(bufferedImage, "PNG", fileOutputStream);
      fileBate = fileOutputStream.toByteArray();
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    } finally {
      ppt.dispose();
    }
    return fileBate;
  }

  public static byte[] convertPptOnePageToPdf(byte[] fileBate, int page) throws Office2PdfException {
    Presentation ppt = getPpt(fileBate);
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      int[] page2 = {page};
      ppt.save(fileOutputStream, page2, SaveFormat.Pdf);
      fileBate = fileOutputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    } finally {
      ppt.dispose();
    }
    return fileBate;
  }
}
