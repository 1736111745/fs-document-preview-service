package com.facishare.document.preview.convert.office.utils;


import com.aspose.words.Document;
import com.aspose.words.FileFormatInfo;
import com.aspose.words.FileFormatUtil;
import com.aspose.words.License;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author AnDy
 */
@Slf4j
public class WordObjectUtil {

  public void getWordsLicense() {
    try(InputStream is = WordObjectUtil.class.getClassLoader().getResourceAsStream("license.xml");) {
      License license = new com.aspose.words.License();
      license.setLicense(is);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public com.aspose.words.Document getWord(ByteArrayInputStream fileStream){
    getWordsLicense();
    Document document = null;
    try {
      document = new Document(fileStream);
    } catch (Exception e) {
      //todo:处理异常，调用加密检查方法
      log.info("The document is encrypted and cannot be previewed");
      e.printStackTrace();
    }
    return  document;
  }

  @SneakyThrows
  public boolean isCheckEncrypt(InputStream file){
    // 如果文件加密 返回false
    FileFormatInfo fileFormatInfo = FileFormatUtil.detectFileFormat(file);
    file.reset();

    boolean encrypted=fileFormatInfo.isEncrypted();

    int loadFormat =fileFormatInfo.getLoadFormat();
    if (encrypted) return true;
    return loadFormat != 10 && loadFormat != 20;
  }

  public int getPageCount(ByteArrayInputStream fileStream){
    return getPageCount(getWord(fileStream));
  }

  @SneakyThrows
  public int getPageCount(Document doc){
    if (doc==null){
      return 0;
    }
    return doc.getPageCount();
  }
}
