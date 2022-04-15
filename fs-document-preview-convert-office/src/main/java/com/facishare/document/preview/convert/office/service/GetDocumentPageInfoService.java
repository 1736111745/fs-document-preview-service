package com.facishare.document.preview.convert.office.service;

import com.aspose.cells.Workbook;
import com.aspose.cells.WorksheetCollection;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.facishare.document.preview.convert.office.model.PageInfo;
import com.facishare.document.preview.convert.office.utils.GetExcelDocumentObject;
import com.facishare.document.preview.convert.office.utils.GetPageInfo;
import com.facishare.document.preview.convert.office.utils.GetPdfDocumentObject;
import com.facishare.document.preview.convert.office.utils.GetPptDocumentObject;
import com.facishare.document.preview.convert.office.utils.GetWordsDocumentObject;
import com.facishare.document.preview.convert.office.utils.ParameterCalibration;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Andy
 * @date 2022年4月12日
 */
@Service
public class GetDocumentPageInfoService {

  public PageInfo getPageInfo(byte[] data, String filePath) {
    PageInfo pageInfo;
    GetPageInfo getPageInfo=new GetPageInfo();
    String fileSuffix = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
    if (!fileSuffix.isEmpty()) {
      switch (fileSuffix) {
        case ".doc":
        case ".docx":
          pageInfo = getWordPageInfo(data, filePath);
          break;
        case ".xls":
        case ".xlsx":
          pageInfo = getExcelPageInfo(data, filePath);
          break;
        case ".ppt":
        case ".pptx":
          pageInfo = getPptPageInfo(data, filePath);
          break;
        case ".pdf":
          pageInfo = getPdfPageInfo(data, filePath);
          break;
        default:
          pageInfo = GetPageInfo.getFalsePageInfo("不支持的文件类型");
      }
    } else {
      pageInfo = GetPageInfo.getFalsePageInfo("文件后缀名为空");
    }
    return pageInfo;
  }

  private PageInfo getWordPageInfo(byte[] data, String filePath) {
    GetPageInfo getPageInfo=new GetPageInfo();
    PageInfo pageInfo = new PageInfo();
    try {
      if (!ParameterCalibration.checkIsEncrypt(data, filePath)) {
        GetWordsDocumentObject getWordsDocumentObject=new GetWordsDocumentObject();
        // 根据传来的文件，创建文档对象
        Document doc = getWordsDocumentObject.getWord(data);
        // 通过Word文档对象获得页码
        pageInfo.setPageCount(doc.getPageCount());
        pageInfo.setSuccess(true);
      } else {
        pageInfo = GetPageInfo.getFalsePageInfo("FILE_ENCRYPT_ERROR_MSG");
      }
    } catch (Exception e) {
      pageInfo = GetPageInfo.getFalsePageInfo("FILE_ENCRYPT_ERROR_MSG");
    }
    return pageInfo;
  }

  private PageInfo getExcelPageInfo(byte[] data, String filePath) {
    GetPageInfo getPageInfo=new GetPageInfo();
    PageInfo pageInfo = new PageInfo();
    try {
      if (!ParameterCalibration.checkIsEncrypt(data, filePath)) {
        GetExcelDocumentObject getExcelDocumentObject=new GetExcelDocumentObject();
        Workbook workbook = getExcelDocumentObject.getWorkBook(data);
        WorksheetCollection worksheetCollection =getExcelDocumentObject.getWorksheetCollection(workbook);
        List<String> sheetNames = getExcelDocumentObject.getSheetNames(worksheetCollection);
        int pageCount = worksheetCollection.getCount();
        pageInfo = GetPageInfo.getExcelPageInfo(pageCount, sheetNames);
        workbook.dispose();
      } else {
        pageInfo = GetPageInfo.getFalsePageInfo("FILE_ENCRYPT_ERROR_MSG");
      }
    } catch (Exception e) {
      pageInfo = GetPageInfo.getFalsePageInfo("FILE_DAMAGE_ERROR_MSG");
    }
    return pageInfo;
  }

  private PageInfo getPptPageInfo(byte[] data, String filePath) {
    GetPageInfo getPageInfo=new GetPageInfo();
    PageInfo pageInfo = new PageInfo();
    try {
      if (!ParameterCalibration.checkIsEncrypt(data, filePath)) {
        GetPptDocumentObject getPptDocumentObject=new GetPptDocumentObject();
        // 获得PPT总页码
        Presentation ppt = getPptDocumentObject.getPpt(data);
        int pageCount = ppt.getSlides().size();
        if (pageCount == 0) {
          pageInfo = GetPageInfo.getFalsePageInfo("The page number is 0");
        } else {
          pageInfo = GetPageInfo.getTruePageInfo(pageCount);
        }
      } else {
        pageInfo = GetPageInfo.getFalsePageInfo("The document is encrypted or corrupted");
      }
    } catch (Exception e) {
      pageInfo = GetPageInfo.getFalsePageInfo("Document instantiation exception");
    }
    return pageInfo;
  }

  private PageInfo getPdfPageInfo(byte[] data, String filePath){
    GetPageInfo getPageInfo=new GetPageInfo();
    PageInfo pageInfo = new PageInfo();
    try {
      if (!ParameterCalibration.checkIsEncrypt(data, filePath)) {
        GetPdfDocumentObject getPdfDocumentObject=new GetPdfDocumentObject();
        // 获得PDF总页码
        com.aspose.pdf.Document pdf = getPdfDocumentObject.getPdf(data);
        int pageCount = pdf.getPages().size();
        if (pageCount == 0) {
          pageInfo = GetPageInfo.getFalsePageInfo("FILE_DAMAGE_ERROR_MSG");
        } else {
          pageInfo = GetPageInfo.getTruePageInfo(pageCount);
        }
        pdf.close();
      } else {
        pageInfo = GetPageInfo.getFalsePageInfo("FILE_DAMAGE_ERROR_MSG");
      }
    } catch (Exception e) {
      pageInfo = GetPageInfo.getFalsePageInfo("FILE_DAMAGE_ERROR_MSG");
    }
    return pageInfo;
  }
}
