package com.facishare.document.preview.convert.office.service.impl;

import com.aspose.cells.Workbook;
import com.aspose.pdf.Document;
import com.aspose.slides.Presentation;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.service.DocumentPageInfoService;
import com.facishare.document.preview.convert.office.utils.ExcelObjectUtil;
import com.facishare.document.preview.convert.office.utils.PageInfoUtil;
import com.facishare.document.preview.convert.office.utils.ParameterCalibrationUtil;
import com.facishare.document.preview.convert.office.utils.PdfObjectUtil;
import com.facishare.document.preview.convert.office.utils.PptObjectUtil;
import com.facishare.document.preview.convert.office.utils.WordObjectUtil;
import java.io.ByteArrayInputStream;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Andy
 */
@Slf4j
@Service
public class DocumentPageInfoServiceImpl implements DocumentPageInfoService {

  public PageInfo getPageInfo(String filePath, ByteArrayInputStream fileStream) {
    switch (FileTypeEnum.valueOf(
        ParameterCalibrationUtil.isDifference(filePath, fileStream).toUpperCase())) {
      case DOC:
      case DOCX:
        return getWordPageInfo(fileStream);
      case XLS:
      case XLSX:
        return getExcelPageInfo(fileStream);
      case PPT:
      case PPTX:
        return getPptPageInfo(fileStream);
      case PDF:
        return getPdfPageInfo(fileStream);
      default:
        return PageInfoUtil.getFalsePageInfo("Unsupported file type");
    }
  }


  @SneakyThrows
  public PageInfo getWordPageInfo(ByteArrayInputStream fileStream) {
    WordObjectUtil wordObjectUtil = new WordObjectUtil();
    // 根据传来的文件，创建文档对象
    int pageCount = wordObjectUtil.getPageCount(fileStream);
    if (pageCount == 0) {
      return PageInfoUtil.getFalsePageInfo("The page number is 0");
    }
    // 通过Word文档对象获得页码
    return PageInfoUtil.getTruePageInfo(pageCount);
  }

  public PageInfo getExcelPageInfo(ByteArrayInputStream fileStream) {
    ExcelObjectUtil excelObjectUtil = new ExcelObjectUtil();
    Workbook workbook = excelObjectUtil.getWorkBook(fileStream);
    if (workbook == null) {
      return PageInfoUtil.getFalsePageInfo("Instantiate Excel document exception, object is empty");
    }
    List<String> sheetNames = excelObjectUtil.getSheetNames(workbook);
    int pageCount = excelObjectUtil.getPageCount(workbook);
    if (pageCount == 0) {
      return PageInfoUtil.getFalsePageInfo("The page number is 0");
    }
    return PageInfoUtil.getExcelPageInfo(pageCount, sheetNames);
  }

  public PageInfo getPptPageInfo(ByteArrayInputStream fileStream) {
    PptObjectUtil pptObjectUtil = new PptObjectUtil();
    Presentation presentation = pptObjectUtil.getPpt(fileStream);
    if (presentation == null) {
      return PageInfoUtil.getFalsePageInfo("Instantiate PPT document exception, object is empty");
    }
    int pageCount = pptObjectUtil.getPageCount(presentation);
    if (pageCount == 0) {
      return PageInfoUtil.getFalsePageInfo("The page number is 0");
    }
    return PageInfoUtil.getTruePageInfo(pageCount);
  }

  public PageInfo getPdfPageInfo(ByteArrayInputStream fileStream) {
    PdfObjectUtil pdfObjectUtil = new PdfObjectUtil();
    Document pdf=pdfObjectUtil.getPdf(fileStream);
    if (pdf == null) {
      return PageInfoUtil.getFalsePageInfo("Instantiate PDF document exception, object is empty");
    }
    int pageCount = pdfObjectUtil.getPageCount(pdf);
    if (pageCount == 0) {
      return PageInfoUtil.getFalsePageInfo("The page number is 0");
    }
    return PageInfoUtil.getTruePageInfo(pageCount);
  }
}
