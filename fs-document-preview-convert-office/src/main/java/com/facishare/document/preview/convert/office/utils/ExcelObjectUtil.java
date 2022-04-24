package com.facishare.document.preview.convert.office.utils;

import com.aspose.cells.FileFormatInfo;
import com.aspose.cells.FileFormatUtil;
import com.aspose.cells.License;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Andy
 */
@Slf4j
public class ExcelObjectUtil {

  @SneakyThrows
  public void getExcelLicense(){
    try(InputStream is = ExcelObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")){
      License license = new License();
      license.setLicense(is);
    } catch (IOException e) {
      log.info("We experienced an exception while verifying the Excel file signature",e);
    }
  }

  public com.aspose.cells.Workbook getWorkBook(ByteArrayInputStream fileStream){
    getExcelLicense();
    Workbook workbook= null;
    try {
      workbook = new Workbook(fileStream);
    } catch (Exception e) {
      //todo:处理异常，调用加密检查方法
      log.info("The document is encrypted and cannot be previewed");
      e.printStackTrace();
    }
    return  workbook;
  }

  public List<String> getSheetNames(com.aspose.cells.Workbook  workbook) {
    WorksheetCollection worksheetCollection=workbook.getWorksheets();
    List<String> sheetNames=new ArrayList<>();
    for (int i = 0; i < worksheetCollection.getCount(); i++) {
      Worksheet worksheet = worksheetCollection.get(i);
      //获得当前工作表的名称
      String sheetName = worksheet.getName();
      //判断当前工作表是否可见 可见返回true 这里boolean值取反
      boolean isHidden = !worksheet.isVisible();
      // 工作表可见 取_$h0$ 工作表不可见取_$h1$
      String hiddenFlag = isHidden ? "_$h1$" : "_$h0$";
      //判断当前活动的表的索引是否与遍历到的索引相一致 一致返回true
      boolean isActive = worksheetCollection.getActiveSheetIndex() == i;
      // 如果当前遍历的表就是活动的表，取_$a1$ 否则取_$a0$
      String activeFlag = isActive ? "_$a1$" : "_$a0$";
      //
      sheetName = sheetName + hiddenFlag + activeFlag;
      sheetNames.add(sheetName);
    }
    return sheetNames;
  }

  public com.aspose.cells.Worksheet getWorkSheet(ByteArrayInputStream fileStream,int page) throws Exception {
    WorksheetCollection worksheetCollection=getWorksheetCollection(fileStream);
    int pageCount=worksheetCollection.getCount();
    if (page>pageCount||page<0){
      return null;
    }
    return worksheetCollection.get(page);
  }


  public com.aspose.cells.WorksheetCollection getWorksheetCollection(ByteArrayInputStream fileStream) throws Exception {
    return getWorkBook(fileStream).getWorksheets();
  }

  public int getPageCount(com.aspose.cells.Workbook workbook){
    int pageCount= workbook.getWorksheets().getCount();
    workbook.dispose();
    return pageCount;
  }

  @SneakyThrows
  public boolean isCheckEncrypt(ByteArrayInputStream fileStream){
    FileFormatInfo fileFormatInfo = FileFormatUtil.detectFileFormat(fileStream);
    boolean encrypted=fileFormatInfo.isEncrypted();
    int loadFormat =fileFormatInfo.getLoadFormat();
    // 如果文件加密 返回true
    if (encrypted) return true;
    return loadFormat != 5 && loadFormat != 6;
  }
}
