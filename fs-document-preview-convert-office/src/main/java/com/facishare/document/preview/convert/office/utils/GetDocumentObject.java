package com.facishare.document.preview.convert.office.utils;


import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AnDy
 */
@Slf4j
@Component
public class GetDocumentObject {

  public com.aspose.words.Document getWord(byte[] data) {
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    com.aspose.words.Document doc = null;
    try {
      doc = new com.aspose.words.Document(fileInputStream);
    } catch (Exception e) {
      log.error("获得Word-Document对象异常：" + e);
    }
    return doc;
  }

  public com.aspose.slides.Presentation getPpt(byte[] data) {
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    com.aspose.slides.Presentation ppt;
    ppt = new com.aspose.slides.Presentation(fileInputStream);
    return ppt;
  }

  public com.aspose.pdf.Document getPdf(byte[] data) {
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    com.aspose.pdf.Document pdf;
    pdf = new com.aspose.pdf.Document(fileInputStream);
    return pdf;
  }

  public com.aspose.cells.WorksheetCollection getWorksheetCollection(byte[] data) {
    Workbook workbook=getWorkBook(data);
    return workbook.getWorksheets();
  }

  public com.aspose.cells.Workbook getWorkBook(byte[] data) {
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    com.aspose.cells.Workbook workBook = new Workbook();
    try {
      workBook = new com.aspose.cells.Workbook(fileInputStream);
    } catch (Exception e) {
      log.error("获得Word-Document对象异常：" + e);
    }
    return workBook;
  }

  public List<String> getSheetNames(com.aspose.cells.WorksheetCollection worksheetCollection) {
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

  public com.aspose.cells.Worksheet getWorkSheet(byte[] data,int page){
    Workbook workbook=getWorkBook(data);
    WorksheetCollection worksheetCollection=getWorksheetCollection(workbook);
    int pageCount=worksheetCollection.getCount();
    if (page>pageCount||page<0){
      return null;
    }
    return worksheetCollection.get(page);
  }

  public com.aspose.cells.WorksheetCollection getWorksheetCollection(com.aspose.cells.Workbook workbook) {
    return workbook.getWorksheets();
  }

}
