package com.facishare.document.preview.convert.office.utils;

import com.aspose.pdf.Document;
import com.aspose.pdf.License;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import java.io.InputStream;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [在类加载时将Aspose签名文件加载入内存中]
 * @createTime : [2022/5/9 14:54]
 * @updateUser : [Andy]
 * @updateTime : [2022/5/9 14:54]
 * @updateRemark : [重构Aspose签名文件加载方式]
 */
public class InitializeAsposePdfUtil {

  /**
   * 防止反射攻击
   */

  private InitializeAsposePdfUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  /*
    在类加载时 加载PDF签名文件
   */

  static {
    try (InputStream is = InitializeAsposePdfUtil.class.getClassLoader()
        .getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PDF_ABNORMAL_FILE_SIGNATURE, e);
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
}
