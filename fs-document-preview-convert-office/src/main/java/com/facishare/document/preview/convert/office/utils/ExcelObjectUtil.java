package com.facishare.document.preview.convert.office.utils;

import com.aspose.cells.FileFormatUtil;
import com.aspose.cells.License;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.AsposeInstantiationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy
 */
public class ExcelObjectUtil {


  public static PageInfo getSheetNames(ByteArrayInputStream fileStream) throws Exception {
    Workbook workbook = getWorkBook(fileStream);
    WorksheetCollection worksheetCollection = workbook.getWorksheets();
    List<String> sheetNames = new ArrayList<>();
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
    return PageInfoUtil.getExcelPageInfo(getPageCount(workbook), sheetNames);

  }

  public static Workbook getWorkBook(ByteArrayInputStream fileStream) throws AsposeInstantiationException {
    getExcelLicense();
    Workbook workbook;
    try {
      workbook = new Workbook(fileStream);
    } catch (Exception e) {
      if (isCheckEncrypt(fileStream)) {
        throw new AsposeInstantiationException(ErrorInfoEnum.EXCEL_ENCRYPTION_ERROR, e);
      }
      throw new AsposeInstantiationException(ErrorInfoEnum.EXCEL_INSTANTIATION_ERROR, e);
    }
    return workbook;
  }

  public static int getPageCount(Workbook workbook) throws Exception {
    if (workbook == null) {
      return 0;
    }
    int pageCount = workbook.getWorksheets().getCount();
    workbook.dispose();
    return pageCount;
  }

  public static void getExcelLicense() throws AsposeInstantiationException {
    try (InputStream is = ExcelObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (IOException e) {
      throw new AsposeInstantiationException(ErrorInfoEnum.ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  public static boolean isCheckEncrypt(ByteArrayInputStream fileStream) throws AsposeInstantiationException {
    //文件加密返回 true
    try {
      return FileFormatUtil.detectFileFormat(fileStream).isEncrypted();
    } catch (Exception e) {
      throw new AsposeInstantiationException(ErrorInfoEnum.EXCEL_INSTANTIATION_ERROR, e);
    }
  }

  public static Worksheet getWorkSheet(ByteArrayInputStream fileStream, int page) throws Exception {
    WorksheetCollection worksheetCollection = getWorksheetCollection(fileStream);
    int pageCount = worksheetCollection.getCount();
    if (page > pageCount || page < 0) {
      return null;
    }
    return worksheetCollection.get(page);
  }

  public static WorksheetCollection getWorksheetCollection(ByteArrayInputStream fileStream) throws Exception {
    return getWorkBook(fileStream).getWorksheets();
  }

  public static int getPageCount(ByteArrayInputStream fileStream) throws Exception {
    return getPageCount(getWorkBook(fileStream));
  }

}
