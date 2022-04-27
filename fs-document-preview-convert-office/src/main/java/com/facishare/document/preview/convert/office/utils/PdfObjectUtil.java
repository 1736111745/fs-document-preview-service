package com.facishare.document.preview.convert.office.utils;

import com.aspose.pdf.Document;
import com.aspose.pdf.License;
import com.aspose.pdf.devices.PngDevice;
import com.aspose.pdf.devices.Resolution;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Andy
 */
@Slf4j
public class PdfObjectUtil {

  public static int getPageCount(byte[] fileBate) {
    return getPageCount(getPdf(fileBate));
  }

  public static int getPageCount(Document pdf) {
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

  public static byte[] convertPdfAllPageToPng(byte[] fileBate, String office2PngTempPath, String office2PngZipTempPath) throws Office2PdfException {
    office2PngTempPath = FileProcessingUtil.createDirectory(office2PngTempPath);
    office2PngZipTempPath = FileProcessingUtil.createDirectory(office2PngZipTempPath);
    Document pdf = getPdf(fileBate);
    int pageCount = pdf.getPages().size();
    Resolution imageResolution = new Resolution(128);
    PngDevice rendererPng = new PngDevice(imageResolution);
    //pdf 下标从1开始
    for (int i = 1; i <= pageCount; i++) {
      try {
        rendererPng.process(pdf.getPages().get_Item(i), Files.newOutputStream(Paths.get(office2PngTempPath + "\\" + i + ".png")));
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.PDF_FILE_SAVING_PNG_FAILURE, e);
      }
    }
    pdf.close();
    return FileProcessingUtil.getZipByte(office2PngTempPath, office2PngZipTempPath);
  }

  public static byte[] convertPdfOnePageToPng(byte[] fileBate, int page) {
    Document pdf = getPdf(fileBate);
    Resolution imageResolution = new Resolution(128);
    PngDevice rendererPng = new PngDevice(imageResolution);
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      rendererPng.process(pdf.getPages().get_Item(page), fileOutputStream);
      fileBate = fileOutputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    } finally {
      pdf.close();
    }
    return fileBate;
  }
}
