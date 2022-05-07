package com.facishare.document.preview.convert.office.utils;


import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.License;
import com.aspose.words.PageSet;
import com.aspose.words.PdfSaveOptions;
import com.aspose.words.SaveFormat;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Andy
 */
public class WordObjectUtil {

  /**
   * 防止反射攻击
   */
  private WordObjectUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  /*
    在类加载时 加载Word签名文件
   */
  static {
    try (InputStream is = WordObjectUtil.class.getClassLoader()
        .getResourceAsStream("license.xml")) {
      License license = new com.aspose.words.License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  /**
   * 获取Word 文档对象 本类的其他方法都依赖于当前方法
   */
  public static Document getWord(MultipartFile file) throws Office2PdfException {
    try (InputStream fileInputStream = file.getInputStream()) {
      return new Document(fileInputStream);
    } catch (Exception e) {
      //如果加载不了，直接抛文件损坏了
      throw new Office2PdfException(ErrorInfoEnum.WORD_INSTANTIATION_ERROR, e);
    }
  }

  /**
   * 对外暴露的 可调用的 获取Word文档 总的页码数量
   */
  public static int getPageCount(MultipartFile file) {
    return getPageCount(getWord(file));
  }

  /**
   * 内部的获取页码的方法 并将doc赋值为null 尽快让其释放
   */
  public static int getPageCount(Document doc) throws Office2PdfException {
    try {
      return doc.getPageCount();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ZERO, e);
    } finally {
      doc = null;
    }
  }

  /**
   *
   */

  public static ByteArrayOutputStream convertDocToPdf(MultipartFile file,ByteArrayOutputStream fileOutputStream) {
    Document doc = getWord(file);
    try{
      doc.save(fileOutputStream, SaveFormat.PDF);
      return fileOutputStream;
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_FAILURE, e);
    }
  }

  public static ByteArrayOutputStream  convertDocToDocx(MultipartFile file, ByteArrayOutputStream fileOutputStream) {
    Document doc = getWord(file);
    try {
      doc.save(fileOutputStream, SaveFormat.DOCX);
      return fileOutputStream;
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_FAILURE, e);
    } finally {
      doc = null;
    }
  }

  public static byte[] convertWordAllPageToPng(MultipartFile file, String office2PngTempPath,
      String office2PngZipTempPath) {
    office2PngTempPath = FileProcessingUtil.createDirectory(office2PngTempPath);
    office2PngZipTempPath = FileProcessingUtil.createDirectory(office2PngZipTempPath);
    Document doc = getWord(file);
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


  public static ByteArrayOutputStream convertWordOnePageToPng(MultipartFile file, int page,ByteArrayOutputStream fileOutputStream) {
    Document doc = getWord(file);
    ImageSaveOptions imageOptions = new ImageSaveOptions(SaveFormat.PNG);
    imageOptions.setUseHighQualityRendering(true);
    imageOptions.setPageSet(new PageSet(page));
    try{
      doc.save(fileOutputStream, imageOptions);
      return fileOutputStream;
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    }
  }

  public static ByteArrayOutputStream convertWordOnePageToPdf(MultipartFile file, int page,ByteArrayOutputStream fileOutputStream)
      throws Office2PdfException {
    Document doc = getWord(file);
    PdfSaveOptions pdfSaveOptions = new com.aspose.words.PdfSaveOptions();
    pdfSaveOptions.setPageSet(new PageSet(page));
    //0 嵌入所有字体 1 嵌入了除标准Windows字体Arial和Times New Roman之外的所有字体  2 不嵌入任何字体。
    pdfSaveOptions.setFontEmbeddingMode(0);
    //0 文档的显示方式留给 PDF 查看器。通常，查看器会显示适合页面宽度的文档。 1 使用指定的缩放系数显示页面。 2 显示页面，使其完全可见。 3 适合页面的宽度。 4 适合页面的高度。 5 适合边界框（包含页面上所有可见元素的矩形）。
    pdfSaveOptions.setZoomBehavior(0);
    try{
      doc.save(fileOutputStream, pdfSaveOptions);
      return fileOutputStream;
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    }
  }
}
