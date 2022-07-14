package com.facishare.document.preview.convert.office.utils;


import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.PresentationFactory;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.Office2PdfException;
import com.facishare.document.preview.convert.office.domain.PageInfo;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

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

  public static byte[] convertPptOnePageToPng(InputStream file,int page){
    Presentation ppt = PptUtil.getPpt(file);
    Dimension size = new Dimension(1280, 720);
    com.aspose.slides.ISlide slide = ppt.getSlides().get_Item(page);
    BufferedImage bufferedImage = slide.getThumbnail(size);
    try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
      ImageIO.write(bufferedImage, "PNG", outputStream);
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    } finally {
      bufferedImage.getGraphics().dispose();
      ppt.dispose();
    }
  }

  public static byte[] convertPptAllPageToPng(InputStream file) {
    String office2PngTempPath = FileProcessingUtil.createDirectory("/opt/office2Png");
    String office2PngZipTempPath = FileProcessingUtil.createDirectory("/opt/office2PngZip");
    Presentation ppt = PptUtil.getPpt(file);
    BufferedImage bufferedImage=null;
    try{
      for (com.aspose.slides.ISlide slide : ppt.getSlides()) {
        Dimension size = new Dimension(1280, 720);
        //设置生成图片的大小
        bufferedImage = slide.getThumbnail(size);
        File outputFile = new File(office2PngTempPath + "\\" + slide.getSlideNumber() + ".png");
        ImageIO.write(bufferedImage, "PNG", outputFile);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_PNG_FAILURE, e);
    } finally {
      if (bufferedImage!=null){
        bufferedImage.getGraphics().dispose();
      }
      ppt.dispose();
    }
    return FileProcessingUtil.getZipByte(office2PngTempPath, office2PngZipTempPath);
  }





}
