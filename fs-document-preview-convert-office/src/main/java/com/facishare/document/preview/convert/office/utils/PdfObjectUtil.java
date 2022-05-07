package com.facishare.document.preview.convert.office.utils;

import com.aspose.pdf.Document;
import com.aspose.pdf.License;
import com.aspose.pdf.devices.PngDevice;
import com.aspose.pdf.devices.Resolution;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Andy
 */
@Slf4j
public class PdfObjectUtil {

  /**
   * 防止反射攻击
   */

  private PdfObjectUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  /*
    在类加载时 加载PDF签名文件
   */

  static {
    try (InputStream is = PdfObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  /**
   * 获取PDF 文档对象 本类的其他方法都依赖于当前方法
   */
  public static Document getPdf(MultipartFile file) throws Office2PdfException {
    try (InputStream fileInputStream = file.getInputStream()) {
      return new Document(fileInputStream);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PDF_INSTANTIATION_ERROR, e);
    }
  }

  /**
   * 对外暴露的 可调用的 获取PDF文档 总的页码数量
   */
  public static int getPageCount(MultipartFile file) {
    return getPageCount(getPdf(file));
  }

  /**
   * 内部的获取页码的方法 并将doc赋值为null 尽快让其释放
   */
  public static int getPageCount(Document pdf) {
    try {
      return pdf.getPages().size();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ZERO, e);
    } finally {
      pdf.close();
    }
  }


  public static byte[] convertPdfAllPageToPng(MultipartFile file, String office2PngTempPath,
      String office2PngZipTempPath) throws Office2PdfException {
    office2PngTempPath = FileProcessingUtil.createDirectory(office2PngTempPath);
    office2PngZipTempPath = FileProcessingUtil.createDirectory(office2PngZipTempPath);
    Document pdf = getPdf(file);
    int pageCount = pdf.getPages().size();
    Resolution imageResolution = new Resolution(128);
    PngDevice rendererPng = new PngDevice(imageResolution);
    //pdf 下标从1开始
    for (int i = 1; i <= pageCount; i++) {
      try {
        rendererPng.process(pdf.getPages().get_Item(i),
            Files.newOutputStream(Paths.get(office2PngTempPath + "\\" + i + ".png")));
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.PDF_FILE_SAVING_PNG_FAILURE, e);
      }
    }
    pdf.close();
    return FileProcessingUtil.getZipByte(office2PngTempPath, office2PngZipTempPath);
  }

  public static ByteArrayOutputStream convertPdfOnePageToPng(MultipartFile file, int page,ByteArrayOutputStream fileOutputStream) {
    Document pdf = getPdf(file);
    Resolution imageResolution = new Resolution(128);
    PngDevice rendererPng = new PngDevice(imageResolution);
    try{
      rendererPng.process(pdf.getPages().get_Item(page), fileOutputStream);
      return fileOutputStream;
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    } finally {
      pdf.close();
    }
  }
}
