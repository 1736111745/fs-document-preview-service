package com.facishare.document.preview.convert.office.utils;

import com.aspose.words.Document;
import com.aspose.words.FileFormatUtil;
import com.aspose.words.License;
import com.aspose.words.LoadOptions;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Andy
 */
public class InitializeAsposeWordUtil {

  /**
   * 防止反射攻击
   */
  private InitializeAsposeWordUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  /*
    在类加载时 加载Word签名文件
   */
  static {
    try (InputStream is = InitializeAsposeWordUtil.class.getClassLoader()
        .getResourceAsStream("license.xml")) {
      License license = new com.aspose.words.License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  /**
   * 获取Word 文档对象 本类的其他方法都依赖于当前方法
   */
  public static Document getWord(InputStream file) throws Office2PdfException {
    try{
      return new Document(file);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_INSTANTIATION_ERROR, e);
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

  public static Document getWord(InputStream file, LoadOptions options) throws Office2PdfException {
    try {
      return new Document(file,options);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_INSTANTIATION_ERROR, e);
    }
  }

}
