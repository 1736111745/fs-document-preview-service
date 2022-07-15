package com.facishare.document.preview.convert.office.utils;

import com.aspose.pdf.Document;
import com.aspose.pdf.License;
import com.aspose.pdf.Page;
import com.aspose.pdf.devices.PngDevice;
import com.aspose.pdf.devices.Resolution;
import com.aspose.pdf.facades.PdfFileInfo;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.Office2PdfException;
import com.facishare.document.preview.convert.office.domain.PageInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

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

  /**
   * 获取PDF 文档对象 本类的其他方法都依赖于当前方法
   */
  public static Document getPdf(InputStream file) throws Office2PdfException {
    try{
      return new Document(file);
    } catch (Exception e) {
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
    try (Document pdf = PdfUtil.getPdf(file)) {
      return PageInfo.ok(pdf.getPages().size());
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PDF_PAGE_NUMBER_PARAMETER_ZERO, e);
    }
  }

  public static byte[] convertPdfOnePageToPng(InputStream file,int page){
    com.aspose.pdf.Document pdf = PdfUtil.getPdf(file);
    com.aspose.pdf.devices.Resolution imageResolution = new com.aspose.pdf.devices.Resolution(128);
    com.aspose.pdf.devices.PngDevice rendererPng = new com.aspose.pdf.devices.PngDevice(imageResolution);
    try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream();Page pdfPage= pdf.getPages().get_Item(page)){
      rendererPng.process(pdfPage, outputStream);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    } finally {
      pdf.close();
    }
  }

  public static byte[] convertPdfAllPageToPng(InputStream file) {
    String office2PngTempPath = FileProcessingUtil.createDirectory("/opt/office2Png");
    String office2PngZipTempPath = FileProcessingUtil.createDirectory("/opt/office2PngZip");
    com.aspose.pdf.Document pdf = PdfUtil.getPdf(file);
    int pageCount = pdf.getPages().size();
    Resolution imageResolution = new Resolution(128);
    PngDevice rendererPng = new PngDevice(imageResolution);
    for (int i = 1; i <= pageCount; i++) {
      try(Page pdfPage = pdf.getPages().get_Item(i)) {
          rendererPng.process(pdfPage, Files.newOutputStream(Paths.get(office2PngTempPath + "\\" + i + ".png")));
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.PDF_FILE_SAVING_PNG_FAILURE, e);
      }
    }
    try{
      pdf.close();
    }catch (Exception e){
      throw new Office2PdfException(ErrorInfoEnum.PDF_CLOSE_EXCEPTION, e);
    }
    return FileProcessingUtil.getZipByte(office2PngTempPath, office2PngZipTempPath);
  }


}
