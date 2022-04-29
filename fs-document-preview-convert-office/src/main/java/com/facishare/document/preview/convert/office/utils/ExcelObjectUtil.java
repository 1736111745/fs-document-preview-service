package com.facishare.document.preview.convert.office.utils;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatUtil;
import com.aspose.cells.HtmlSaveOptions;
import com.aspose.cells.License;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy
 */
public class ExcelObjectUtil {
  public static PageInfo getSheetNames(byte[] fileBate) {
    Workbook workbook = getWorkBook(fileBate);
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

  public static Workbook getWorkBook(byte[] fileBate) throws Office2PdfException {
    getExcelLicense();
    Workbook workbook;
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      workbook = new Workbook(fileStream);
    } catch (Exception e) {
      if (isCheckEncrypt(fileBate)) {
        throw new Office2PdfException(ErrorInfoEnum.EXCEL_ENCRYPTION_ERROR, e);
      }
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_INSTANTIATION_ERROR, e);
    }
    return workbook;
  }

  public static void getExcelLicense() throws Office2PdfException {
    try (InputStream is = ExcelObjectUtil.class.getClassLoader().getResourceAsStream("license.xml")) {
      License license = new License();
      license.setLicense(is);
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.ABNORMAL_FILE_SIGNATURE, e);
    }
  }

  public static boolean isCheckEncrypt(byte[] fileBate) throws Office2PdfException {
    //文件加密返回 true
    try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBate)) {
      return FileFormatUtil.detectFileFormat(fileStream).isEncrypted();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_INSTANTIATION_ERROR, e);
    }
  }

  public static int getPageCount(Workbook workbook) {
    if (workbook == null) {
      return 0;
    }
    int pageCount = workbook.getWorksheets().getCount();
    workbook.dispose();
    return pageCount;
  }

  public static int getPageCount(byte[] fileBate) {
    return getPageCount(getWorkBook(fileBate));
  }

  public static byte[] convertXlsToXlsx(byte[] fileBate) {
    Workbook workbook = getWorkBook(fileBate);
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      workbook.save(fileOutputStream, SaveFormat.XLSX);
      fileBate = fileOutputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_FILE_SAVING_FAILURE, e);
    } finally {
      workbook.dispose();
    }
    return fileBate;
  }

  public byte[] getHtml(byte[] fileBate, int page) throws Office2PdfException {
    //下标从0开始，但要求接收的页码从1开始
    Workbook workbook = getWorkBook(fileBate);
    WorksheetCollection worksheetCollection = workbook.getWorksheets();
    Worksheet worksheet = getWorkSheet(worksheetCollection, page);

    //获取最大行数 	包含数据或样式的单元格的最大行索引
    int rows = worksheet.getCells().getMaxRow();
    //获取有数据的行数 并+10 包含数据的单元格的最大行索引
    int validRows = worksheet.getCells().getMaxDataRow() + 10;
    // 有数据行数与最大行数比较，取较小的值
    validRows = Math.min(validRows, rows);
    // 有数据行数与5000相比较，取较小的值
    validRows = Math.min(validRows, 5000);
    int blankRowStart = validRows + 1;
    blankRowStart = Math.max(blankRowStart, 0);
    int blankRowEnd = rows - blankRowStart;
    blankRowEnd = Math.max(blankRowEnd, 0);

    if (blankRowEnd > 0) {
      // 删除表中多行 blankRowsStart要删除的第一行索引 blankRowEnd要删除的行数
      worksheet.getCells().deleteRows(blankRowStart, blankRowEnd, true);
    }
    // 有效列数
    int validColumns = worksheet.getCells().getMaxDataColumn();
    validColumns = Math.min(validColumns, 1000);
    if (blankRowEnd > 0) {
      worksheet.getCells().deleteColumns(blankRowStart, blankRowEnd, true);
    }
    Cells cells = worksheet.getCells();
    for (int col = 0; col < validColumns; col++) {
      //cells.getColumnWidthPixel()获取单元格列的像素
      cells.setColumnWidthPixel(col, (int) (cells.getColumnWidthPixel(col) * 2f));
    }
    // 设置当前活动单元格的索引
    worksheetCollection.setActiveSheetIndex(page);
    HtmlSaveOptions saveOptions = new HtmlSaveOptions();
    // 默认为 hidden 隐藏 即 this.p=0
    // 设置导出时，有无单元格线条 true为有 默认为false
    saveOptions.setExportGridLines(true);
    //当宽度为0时是否隐藏 默认为0
    saveOptions.setHiddenColDisplayType(0);
    saveOptions.setHiddenRowDisplayType(0);
    saveOptions.getImageOptions().setCellAutoFit(true);
    saveOptions.setExportImagesAsBase64(true);
    saveOptions.setCreateDirectory(true);
    saveOptions.setEnableHTTPCompression(true);
    saveOptions.setExportActiveWorksheetOnly(true);
    try (ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream()) {
      // 将文件输出流与定义的HTML文件格式绑定
      workbook.save(fileOutputStream, saveOptions);
      fileBate = fileOutputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_FILE_SAVING_FAILURE, e);
    } finally {
      workbook.dispose();
    }
    return fileBate;
  }

  public static Worksheet getWorkSheet(WorksheetCollection worksheetCollection, int page) throws Office2PdfException {
    Worksheet worksheet;
    try {
      worksheet = worksheetCollection.get(page);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    }
    return worksheet;
  }
}
