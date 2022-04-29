package com.facishare.document.preview.convert.office.utils;


import com.aspose.words.Document;
import com.aspose.words.FileFormatUtil;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.License;
import com.aspose.words.PageSet;
import com.aspose.words.PdfSaveOptions;
import com.aspose.words.SaveFormat;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author AnDy
 */
public class WordObjectUtil {

  private WordObjectUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  public static int getPageCount(byte[] fileBate) {
    return getPageCount(getWord(fileBate));
  }

  public static int getPageCount(Document doc) throws Office2PdfException {
    if (doc == null) {
      return 0;
    }
    int pageCount;
    try {
      pageCount = doc.getPageCount();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ZERO, e);
    }
    return pageCount;
  }

  public static Document getWord(byte[] fileBate) throws Office2PdfException {
    getWordsLicense();
    Document document;
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      document = new Document(fileStream);
    } catch (Exception e) {
      if (isCheckEncrypt(fileBate)) {
        throw new Office2PdfException(ErrorInfoEnum.WORD_ENCRYPTION_ERROR, e);
      }
      throw new Office2PdfException(ErrorInfoEnum.WORD_INSTANTIATION_ERROR, e);
    }
    return document;
  }

  public static void getWordsLicense() throws Office2PdfException {
    try (InputStream is = WordObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new com.aspose.words.License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  public static boolean isCheckEncrypt(byte[] fileBate) throws Office2PdfException {
    // 如果文件加密 返回true
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      return FileFormatUtil.detectFileFormat(fileStream).isEncrypted();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_INSTANTIATION_ERROR, e);
    }
  }

  public static byte[] convertDocToPdf(byte[] fileBate) {
    Document doc = getWord(fileBate);
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    try {
      doc.save(fileOutputStream, SaveFormat.PDF);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_FAILURE, e);
    }
    fileBate = fileOutputStream.toByteArray();
    try {
      fileOutputStream.close();
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.STREAM_CLOSING_ANOMALY, e);
    }
    return fileBate;
  }

  public static byte[] convertDocToDocx(byte[] fileBate) {
    Document doc = getWord(fileBate);
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    try {
      doc.save(fileOutputStream, SaveFormat.DOCX);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_FAILURE, e);
    }
    fileBate = fileOutputStream.toByteArray();
    try {
      fileOutputStream.close();
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.STREAM_CLOSING_ANOMALY, e);
    }
    return fileBate;
  }

  public static byte[] convertWordAllPageToPng(byte[] fileBate, String office2PngTempPath, String office2PngZipTempPath) {
    office2PngTempPath = FileProcessingUtil.createDirectory(office2PngTempPath);
    office2PngZipTempPath = FileProcessingUtil.createDirectory(office2PngZipTempPath);
    Document doc = getWord(fileBate);
    int pageCount = getPageCount(doc);
    for (int i = 0; i < pageCount; i++) {
      String fileName = office2PngTempPath + "\\" + i + ".png";
      ImageSaveOptions imageOptions = new ImageSaveOptions(SaveFormat.PNG);
      imageOptions.setUseHighQualityRendering(true);
      imageOptions.setPageSet(new PageSet(i));
      try {
        doc.save(fileName, imageOptions);
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_PNG_FAILURE, e);
      }
    }
    return FileProcessingUtil.getZipByte(office2PngTempPath, office2PngZipTempPath);
  }


  public static byte[] convertWordOnePageToPng(byte[] fileBate, int page) {
    Document doc = getWord(fileBate);
    ImageSaveOptions imageOptions = new ImageSaveOptions(SaveFormat.PNG);
    imageOptions.setUseHighQualityRendering(true);
    imageOptions.setPageSet(new PageSet(page));
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      doc.save(fileOutputStream, imageOptions);
      fileBate = fileOutputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    }
    return fileBate;
  }

  public static byte[] convertWordOnePageToPdf(byte[] fileBate, int page) throws Office2PdfException {
    Document doc = getWord(fileBate);
    PdfSaveOptions pdfSaveOptions = new com.aspose.words.PdfSaveOptions();
    pdfSaveOptions.setPageSet(new PageSet(page));
    //0 嵌入所有字体 1 嵌入了除标准Windows字体Arial和Times New Roman之外的所有字体  2 不嵌入任何字体。
    pdfSaveOptions.setFontEmbeddingMode(0);
    //0 文档的显示方式留给 PDF 查看器。通常，查看器会显示适合页面宽度的文档。 1 使用指定的缩放系数显示页面。 2 显示页面，使其完全可见。 3 适合页面的宽度。 4 适合页面的高度。 5 适合边界框（包含页面上所有可见元素的矩形）。
    pdfSaveOptions.setZoomBehavior(0);
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      doc.save(fileOutputStream, pdfSaveOptions);
      fileBate = fileOutputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    }
    return fileBate;
  }
}
