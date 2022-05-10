package com.facishare.document.preview.convert.office.utils;

import com.aspose.cells.License;
import com.aspose.cells.Workbook;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Andy
 */
public class InitializeAsposeExcelUtil {

  /**
   * 防止反射攻击
   */
  private InitializeAsposeExcelUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  /*
    在类加载时 加载Excel签名文件
   */
  static {
    try (InputStream is = InitializeAsposeExcelUtil.class.getClassLoader()
        .getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  /**
   * 获取Excel 文档对象 本类的其他方法都依赖于当前方法
   */
  public static Workbook getWorkBook(InputStream file) throws Office2PdfException {
    try{
      return new Workbook(file);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_INSTANTIATION_ERROR, e);
    }
  }
}
