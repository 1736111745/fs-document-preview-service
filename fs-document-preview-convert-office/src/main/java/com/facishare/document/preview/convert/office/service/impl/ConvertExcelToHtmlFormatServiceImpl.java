package com.facishare.document.preview.convert.office.service.impl;

import com.aspose.cells.Cells;
import com.aspose.cells.HtmlSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.facishare.document.preview.convert.office.service.ConvertExcelToHtmlFormatService;
import com.facishare.document.preview.convert.office.utils.InitializeAsposeExcelUtil;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.springframework.stereotype.Service;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [将Excel表格转换为HTML文档页面]
 * @createTime : [2022/5/10 11:24]
 * @updateUser : [Andy]
 * @updateTime : [2022/5/10 11:24]
 * @updateRemark : [将转换为HTML页面的的服务单独拆分为一个服务]
 */
@Service
public class  ConvertExcelToHtmlFormatServiceImpl implements ConvertExcelToHtmlFormatService {

  @Override
  public byte[] convertOnePageExcelToHtml(InputStream file, int page) {
    return getHtml(file, page);
  }

  private byte[] getHtml(InputStream file, int page) {
    //下标从0开始，但要求接收的页码从1开始
    try {
     Workbook workbook = InitializeAsposeExcelUtil.getWorkBook(file);
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
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
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
}
