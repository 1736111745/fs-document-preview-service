package com.facishare.document.preview.convert.office.utils;


import com.aspose.words.Document;
import com.aspose.words.FileFormatUtil;
import com.aspose.words.FontSettings;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.License;
import com.aspose.words.LoadOptions;
import com.aspose.words.PageSet;
import com.aspose.words.PdfSaveOptions;
import com.aspose.words.SaveFormat;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.Office2PdfException;
import com.facishare.document.preview.convert.office.domain.PageInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class WordUtil {

  /**
   * 防止反射攻击
   */
  private WordUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  /*
  在类加载时 加载Word签名文件
 */
  static {
    try (InputStream is = WordUtil.class.getClassLoader()
        .getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  public static Document getWord(byte[] fileBate) throws Office2PdfException {
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      return new Document(fileStream);
    } catch (Exception e) {
      if (isCheckEncrypt(fileBate)) {
        throw new Office2PdfException(ErrorInfoEnum.WORD_ENCRYPTION_ERROR, e);
      }
      throw new Office2PdfException(ErrorInfoEnum.WORD_INSTANTIATION_ERROR, e);
    }
  }

  public static boolean isCheckEncrypt(byte[] fileBate) throws Office2PdfException {
    // 如果文件加密 返回true
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      return FileFormatUtil.detectFileFormat(fileStream).isEncrypted();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_INSTANTIATION_ERROR, e);
    }
  }

  public static Document getWord(byte[] fileBate, LoadOptions options) throws Office2PdfException {
    try(ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)){
      return new Document(fileStream,options);
    } catch (Exception e) {
      if (isCheckEncrypt(fileBate)) {
        throw new Office2PdfException(ErrorInfoEnum.WORD_ENCRYPTION_ERROR, e);
      }
      throw new Office2PdfException(ErrorInfoEnum.WORD_INSTANTIATION_ERROR, e);
    }
  }

  public static Document getWord(InputStream file, LoadOptions options) throws Office2PdfException {
    try {
      return new Document(file,options);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_INSTANTIATION_ERROR, e);
    }
  }

  public static Document getWord(InputStream file) throws Office2PdfException {
    try{
      return new Document(file);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_INSTANTIATION_ERROR, e);
    }
  }

  public static PageInfo getWordPageCount(byte[] file) {
    Document doc =WordUtil.getWord(file);
    try {
      return PageInfo.ok(doc.getPageCount());
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_PAGE_NUMBER_PARAMETER_ZERO, e);
    }
  }

  public static byte[] convertDocToDocx(byte[] file) {
    Document doc = WordUtil.getWord(file);
    try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      doc.save(outputStream, com.aspose.words.SaveFormat.DOCX);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_FAILURE, e);
    }
  }

  public static byte[] convertWordOnePageToPdf(InputStream file, int page) {
    //设置Word文件的默认编码
    LoadOptions options = new LoadOptions();
    options.setEncoding(StandardCharsets.UTF_8);
    Document doc = WordUtil.getWord(file, options);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
      pdfSaveOptions.setPageSet(new com.aspose.words.PageSet(page));
      //0 嵌入所有字体 1 嵌入了除标准Windows字体Arial和Times New Roman之外的所有字体  2 不嵌入任何字体。
      pdfSaveOptions.setFontEmbeddingMode(0);
      //0 文档的显示方式留给 PDF 查看器。通常，查看器会显示适合页面宽度的文档。 1 使用指定的缩放系数显示页面。 2 显示页面，使其完全可见。 3 适合页面的宽度。 4 适合页面的高度。 5 适合边界框（包含页面上所有可见元素的矩形）。
      pdfSaveOptions.setZoomBehavior(0);
      FontSettings.getDefaultInstance().setFontsFolder("/usr/share/fonts", true);
      doc.save(outputStream, pdfSaveOptions);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    }
  }

  public static byte[] convertDocToPdf(InputStream file) {
    LoadOptions options = new LoadOptions();
    options.setEncoding(StandardCharsets.UTF_8);
    Document doc = WordUtil.getWord(file, options);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
      //0 嵌入所有字体 1 嵌入了除标准Windows字体Arial和Times New Roman之外的所有字体  2 不嵌入任何字体。
      pdfSaveOptions.setFontEmbeddingMode(0);
      pdfSaveOptions.setZoomBehavior(0);
      FontSettings.getDefaultInstance().setFontsFolder("/usr/share/fonts", true);
      doc.save(outputStream,pdfSaveOptions);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_FAILURE, e);
    }
  }

  public static byte[] convertWordOnePageToPng(InputStream file,int page){
    Document doc =WordUtil.getWord(file);
    ImageSaveOptions imageOptions = new ImageSaveOptions(SaveFormat.PNG);
    imageOptions.setUseHighQualityRendering(true);
    imageOptions.setPageSet(new PageSet(page));
    try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
      doc.save(outputStream, imageOptions);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    }
  }
}
