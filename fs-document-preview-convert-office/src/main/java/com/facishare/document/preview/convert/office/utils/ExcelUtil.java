package com.facishare.document.preview.convert.office.utils;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatUtil;
import com.aspose.cells.HtmlSaveOptions;
import com.aspose.cells.License;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.Office2PdfException;
import com.facishare.document.preview.convert.office.domain.PageInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

  /**
   * 防止反射攻击
   */
  private ExcelUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  /*
    在类加载时 加载Excel签名文件
   */
  static {
    try (InputStream is = ExcelUtil.class.getClassLoader()
        .getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  public static Workbook getWorkBook(byte[] fileBate) {
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      return new Workbook(fileStream);
    } catch (Exception e) {
      if (isCheckEncrypt(fileBate)) {
        throw new Office2PdfException(ErrorInfoEnum.EXCEL_ENCRYPTION_ERROR, e);
      }
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_INSTANTIATION_ERROR, e);
    }
  }

  public static boolean isCheckEncrypt(byte[] fileBate){
    //文件加密返回 true
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      return FileFormatUtil.detectFileFormat(fileStream).isEncrypted();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_INSTANTIATION_ERROR, e);
    }
  }

  /**
   * 获取Excel 文档对象 本类的其他方法都依赖于当前方法
   */
  public static Workbook getWorkBook(InputStream file){
    try{
      return new Workbook(file);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_INSTANTIATION_ERROR, e);
    }
  }

  public static PageInfo getExcelPageCount(byte[] fileBate) {
    Workbook workbook = ExcelUtil.getWorkBook(fileBate);
    WorksheetCollection worksheetCollection = workbook.getWorksheets();
    int pageCount = worksheetCollection.getCount();
    List<String> sheetNames = new ArrayList<>();
    for (int i = 0; i < pageCount; i++) {
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
    try {
      return PageInfo.ok(pageCount,sheetNames);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_PAGE_NUMBER_PARAMETER_ZERO, e);
    } finally {
      workbook.dispose();
    }
  }

  public static byte[] ExcelToHtml(InputStream file,int page) {
    //下标从0开始，但要求接收的页码从1开始
    try {
      Workbook workbook = ExcelUtil.getWorkBook(file);
      WorksheetCollection worksheetCollection = workbook.getWorksheets();
      try {
        Worksheet worksheet = worksheetCollection.get(page);
        Cells cells = worksheet.getCells();
        int rows = cells.getMaxRow();
        int validRows = cells.getMaxDataRow() + 10;
        validRows = Math.min(validRows, rows);
        validRows = Math.min(validRows, 5000);
        int blankRowStart = validRows + 1;
        blankRowStart = Math.max(blankRowStart, 0);
        int blankRowEnd = rows - blankRowStart;
        blankRowEnd = Math.max(blankRowEnd, 0);
        if (blankRowEnd > 0) {
          worksheet.getCells().deleteRows(blankRowStart, blankRowEnd, true);
        }
        int validColumns = worksheet.getCells().getMaxDataColumn();
        validColumns = Math.min(validColumns, 1000);
        if (blankRowEnd > 0) {
          worksheet.getCells().deleteColumns(blankRowStart, blankRowEnd, true);
        }
        for (int col = 0; col < validColumns; col++) {
          cells.setColumnWidthPixel(col, (int) (cells.getColumnWidthPixel(col) * 2f));
        }
        worksheetCollection.setActiveSheetIndex(page);
        HtmlSaveOptions saveOptions = new HtmlSaveOptions();
        saveOptions.setExportGridLines(true);
        saveOptions.setHiddenColDisplayType(0);
        saveOptions.setHiddenRowDisplayType(0);
        saveOptions.getImageOptions().setCellAutoFit(true);
        saveOptions.setExportImagesAsBase64(true);
        saveOptions.setCreateDirectory(true);
        saveOptions.setEnableHTTPCompression(true);
        saveOptions.setExportActiveWorksheetOnly(true);
        //试验性功能
        saveOptions.setExportHeadings(true);
        //将html或mht文件是表的首选项 获得更漂亮的演示文稿 应该将值设置为true
        saveOptions.setPresentationPreference(true);
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
          workbook.save(outputStream, saveOptions);
          return outputStream.toByteArray();
        } catch (Exception e) {
          throw new Office2PdfException(ErrorInfoEnum.EXCEL_FILE_SAVING_FAILURE, e);
        } finally {
          workbook.dispose();
        }
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
      }
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_INSTANTIATION_ERROR, e);
    }
  }

  public static byte[] convertXlsToXlsx(byte[] file) {
    Workbook workbook =  ExcelUtil.getWorkBook(file);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      workbook.save(outputStream, com.aspose.cells.SaveFormat.XLSX);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_FILE_SAVING_FAILURE, e);
    } finally {
      workbook.dispose();
    }
  }




}
