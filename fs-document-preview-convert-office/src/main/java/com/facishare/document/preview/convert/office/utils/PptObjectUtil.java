package com.facishare.document.preview.convert.office.utils;

import com.aspose.slides.ISlide;
import com.aspose.slides.License;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Andy
 */
public class PptObjectUtil {

  /**
   * 防止反射攻击
   */

  private PptObjectUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  /*
    在类加载时 加载PPT签名文件
   */
  static {
    try (InputStream is = PptObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new com.aspose.slides.License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  /**
   * 获取PPT文档对象 本类的其他方法都依赖于当前方法
   */

  public static Presentation getPpt(MultipartFile file) throws Office2PdfException {
    try (InputStream fileStream = file.getInputStream()) {
      return new Presentation(fileStream);
    } catch (Exception e) {
      //如果加载不了，直接抛文件损坏了
      throw new Office2PdfException(ErrorInfoEnum.PPT_INSTANTIATION_ERROR, e);
    }
  }

  /**
   * 对外暴露的 可调用的 获取PPT文档 总的页码数量
   */
  public static int getPageCount(MultipartFile file) {
    return getPageCount(getPpt(file));
  }

  /**
   * 内部的获取页码的方法,在获取之后直接释放PPT占用的资源
   */

  public static int getPageCount(Presentation ppt) {
    try {
      return ppt.getSlides().size();
    } catch (Exception e){
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ZERO, e);
    }finally {
      ppt.dispose();
    }
  }


  public static ByteArrayOutputStream  convertPptToPptx(MultipartFile file, ByteArrayOutputStream fileOutputStream) {
    Presentation ppt = getPpt(file);
    try {
      ppt.save(fileOutputStream, SaveFormat.Pptx);
      return fileOutputStream;
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PPT_FILE_SAVING_FAILURE, e);
    } finally {
      ppt.dispose();
    }
  }


  public static ByteArrayOutputStream convertPptToPdf(MultipartFile file,ByteArrayOutputStream fileOutputStream) throws Office2PdfException {
    Presentation ppt = getPpt(file);
    try{
      ppt.save(fileOutputStream, SaveFormat.Pdf);
      return fileOutputStream;
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.STREAM_CLOSING_ANOMALY, e);
    } finally {
      ppt.dispose();
    }
  }


  public static byte[] convertPptAllPageToPng(MultipartFile file, String office2PngTempPath,
      String office2PngZipTempPath) {
    office2PngTempPath = FileProcessingUtil.createDirectory(office2PngTempPath);
    office2PngZipTempPath = FileProcessingUtil.createDirectory(office2PngZipTempPath);
    Presentation ppt = getPpt(file);
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

  public static ByteArrayOutputStream convertPptOnePageToPng(MultipartFile file, int page,ByteArrayOutputStream fileOutputStream) {
    Presentation ppt = getPpt(file);
    //设置要转换的图片的格式
    Dimension size = new Dimension(1280, 720);
    //获得指定页码的幻灯片
    ISlide slide = ppt.getSlides().get_Item(page);
    //转换为图片流格式
    BufferedImage bufferedImage = slide.getThumbnail(size);
    try{
      ImageIO.write(bufferedImage, "PNG", fileOutputStream);
      return fileOutputStream;
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    } finally {
      ppt.dispose();
    }
  }

  public static ByteArrayOutputStream convertPptOnePageToPdf(MultipartFile file, int page,ByteArrayOutputStream fileOutputStream)
      throws Office2PdfException {
    Presentation ppt = getPpt(file);
    try{
      int[] page2 = {page};
      ppt.save(fileOutputStream, page2, SaveFormat.Pdf);
      return fileOutputStream;
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    } finally {
      ppt.dispose();
    }
  }
}
