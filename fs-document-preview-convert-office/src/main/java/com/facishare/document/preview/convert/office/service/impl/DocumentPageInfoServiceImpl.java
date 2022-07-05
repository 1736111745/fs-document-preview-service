package com.facishare.document.preview.convert.office.service.impl;


import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.facishare.document.preview.convert.office.model.PageInfo;
import com.facishare.document.preview.convert.office.service.DocumentPageInfoService;
import com.facishare.document.preview.convert.office.utils.InitializeAsposeExcelUtil;
import com.facishare.document.preview.convert.office.utils.InitializeAsposePdfUtil;
import com.facishare.document.preview.convert.office.utils.InitializeAsposePptUtil;
import com.facishare.document.preview.convert.office.utils.InitializeAsposeWordUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author Andy
 */
@Service
public class DocumentPageInfoServiceImpl implements DocumentPageInfoService {

  public PageInfo getPageInfo(byte[] file, FileTypeEnum fileTypeEnum) {
    switch (fileTypeEnum) {
      case DOC:
      case DOCX:
        return getWordPageCount(file);
      case XLS:
      case XLSX:
        return getExcelPageCount(file);
      case PPT:
      case PPTX:
        return getPptPageCount(file);
      default:
        return getPdfPageCount(file);
    }
  }
  @Override
  public PageInfo getWordPageCount(byte[] file) {
    Document doc =InitializeAsposeWordUtil.getWord(file);
    try {
      return new PageInfo(doc.getPageCount());
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_PAGE_NUMBER_PARAMETER_ZERO, e);
    }
  }

  public PageInfo getExcelPageCount(byte[] fileBate) {
    Workbook workbook = InitializeAsposeExcelUtil.getWorkBook(fileBate);
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
    try {
      return new PageInfo(workbook.getWorksheets().getCount(),sheetNames);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.EXCEL_PAGE_NUMBER_PARAMETER_ZERO, e);
    } finally {
      workbook.dispose();
    }
  }

  @Override
  public PageInfo getPptPageCount(byte[] file) {
    Presentation ppt= InitializeAsposePptUtil.getPpt(file);
    try {
      return new PageInfo(ppt.getSlides().size());
    } catch (Exception e){
      throw new Office2PdfException(ErrorInfoEnum.PPT_PAGE_NUMBER_PARAMETER_ZERO, e);
    }finally {
      ppt.dispose();
    }
  }

  @Override
  public PageInfo getPdfPageCount(byte[] file) {
    com.aspose.pdf.Document pdf = InitializeAsposePdfUtil.getPdf(file);
    try{
      return new PageInfo(pdf.getPages().size());
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PDF_PAGE_NUMBER_PARAMETER_ZERO, e);
    }finally {
      pdf.close();
    }
  }
}
