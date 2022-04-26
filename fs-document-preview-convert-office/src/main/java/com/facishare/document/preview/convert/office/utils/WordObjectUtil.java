package com.facishare.document.preview.convert.office.utils;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.aspose.words.Document;
import com.aspose.words.FileFormatUtil;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.License;
import com.aspose.words.PageSet;
import com.aspose.words.SaveFormat;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author AnDy
 */
public class WordObjectUtil {

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
    office2PngTempPath = String.valueOf(Paths.get(office2PngTempPath, UUID.randomUUID().toString()));
    office2PngZipTempPath = String.valueOf(Paths.get(office2PngZipTempPath, UUID.randomUUID().toString()));
    Document doc = getWord(fileBate);
    for (int i = 0; i < getPageCount(doc); i++) {
      String fileName = office2PngTempPath + "\\" + i + ".png";
      ImageSaveOptions imageOptions = new com.aspose.words.ImageSaveOptions(com.aspose.words.SaveFormat.PNG);
      imageOptions.setUseHighQualityRendering(true);
      imageOptions.setPageSet(new PageSet(i));
      try {
        doc.save(fileName, imageOptions);
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_PNG_FAILURE, e);
      }
    }
    try {
      new File(office2PngZipTempPath).mkdirs();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.UNABLE_CREATE_FOLDER);
    }
    String zipFileName = office2PngZipTempPath + "\\" + UUID.randomUUID() + ".zip";
    fileBate = FileUtil.readBytes((ZipUtil.zip(office2PngZipTempPath, zipFileName)));
    deleteTempDirectory(new File(office2PngTempPath));
    deleteTempDirectory(new File(office2PngZipTempPath));
    return fileBate;
  }

  private static void deleteTempDirectory(File file) {
    File[] listFile = file.listFiles();
    if (listFile != null) {
      for (File temp : listFile) {
        deleteTempDirectory(temp);
      }
    }
    file.delete();
  }
}
