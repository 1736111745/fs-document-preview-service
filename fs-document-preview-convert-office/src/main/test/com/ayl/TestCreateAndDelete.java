package com.ayl;

import cn.hutool.core.io.FileUtil;
import com.aspose.words.Document;
import com.aspose.words.FileFormatUtil;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.License;
import com.aspose.words.PageSet;
import com.aspose.words.SaveFormat;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.facishare.document.preview.convert.office.utils.FileProcessingUtil;
import com.facishare.document.preview.convert.office.utils.WordObjectUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestCreateAndDelete {

  @Value("${office2PngTempPath}")
  private String office2PngTempPath;

  @Value("${office2PngZipTempPath}")
  private String office2PngZipTempPath;

  @Test
  public void TestOne() throws IOException {
    String sourceFile = "C:\\Users\\anyl9356\\Documents\\Test\\test.doc";
    byte[] data = FileUtil.readBytes(sourceFile);
    byte[] bytes = convertWordAllPageToPng(data, office2PngTempPath, office2PngZipTempPath);
    String filePath = "C:\\Users\\anyl9356\\Documents\\Test\\image\\test.zip";
    FileOutputStream os = new FileOutputStream(filePath);
    os.write(bytes);
    os.close();
  }

  public static byte[] convertWordAllPageToPng(byte[] fileBate, String office2PngTempPath, String office2PngZipTempPath) {

    office2PngTempPath = FileProcessingUtil.getRandomFilePath(office2PngTempPath);
    office2PngZipTempPath = FileProcessingUtil.getRandomFilePath(office2PngZipTempPath);

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


}
