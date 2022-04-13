package com.facishare.document.preview.convert.office.service;

import com.aspose.cells.License;
import com.aspose.cells.Workbook;
import com.aspose.cells.WorksheetCollection;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.utils.GetDocumentObject;
import com.facishare.document.preview.convert.office.utils.GetPageInfo;
import com.facishare.document.preview.convert.office.utils.ParameterCalibration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

/**
 * @author Andy
 * @date 2022年4月12日
 */
@Service
public class GetDocumentPageInfoService {


  @Resource
  private ParameterCalibration parameterCalibration;

  @Resource
  private GetDocumentObject getDocumentObject;

  @Resource
  private GetPageInfo getPageInfo;

  @Resource
  private PageInfo pageInfo;

  public static boolean getLicense() {
    try {
      InputStream is = GetDocumentPageInfoService.class.getClassLoader().getResourceAsStream("license.xml");
      License aposeLic = new License();
      aposeLic.setLicense(is);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }


  public PageInfo getPageInfo(byte[] data, String filePath) {
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
          pageInfo = getPageInfo.getFalsePageInfo("不支持的文件类型");
      }
    } else {
      pageInfo = getPageInfo.getFalsePageInfo("文件后缀名为空");
    }
    return pageInfo;
  }

  private PageInfo getWordPageInfo(byte[] data, String filePath) {
    PageInfo pageInfo = new PageInfo();
    try {
      if (!parameterCalibration.checkIsEncrypt(data, filePath)) {
        // 根据传来的文件，创建文档对象
        Document doc = getDocumentObject.getWord(data);
        // 通过Word文档对象获得页码
        pageInfo.setPageCount(doc.getPageCount());
        pageInfo.setSuccess(true);
      } else {
        pageInfo = getPageInfo.getFalsePageInfo("FILE_ENCRYPT_ERROR_MSG");
      }
    } catch (Exception e) {
      pageInfo = getPageInfo.getFalsePageInfo("FILE_ENCRYPT_ERROR_MSG");
    }
    return pageInfo;
  }

  private PageInfo getExcelPageInfo(byte[] data, String filePath) {
    try {
      if (!parameterCalibration.checkIsEncrypt(data, filePath)) {
        Workbook workbook = getDocumentObject.getWorkBook(data);
        WorksheetCollection worksheetCollection = getDocumentObject.getWorksheetCollection(workbook);
        List<String> sheetNames = getDocumentObject.getSheetNames(worksheetCollection);
        int pageCount = worksheetCollection.getCount();
        pageInfo = getPageInfo.getExcelPageInfo(pageCount, sheetNames);
        workbook.dispose();
      } else {
        pageInfo = getPageInfo.getFalsePageInfo("FILE_ENCRYPT_ERROR_MSG");
      }
    } catch (Exception e) {
      pageInfo = getPageInfo.getFalsePageInfo("FILE_DAMAGE_ERROR_MSG");
    }
    return pageInfo;
  }

  private PageInfo getPptPageInfo(byte[] data, String filePath) {
    try {
      if (!parameterCalibration.checkIsEncrypt(data, filePath)) {
        // 获得PPT总页码
        Presentation ppt = getDocumentObject.getPpt(data);
        int pageCount = ppt.getSlides().size();
        if (pageCount == 0) {
          pageInfo = getPageInfo.getFalsePageInfo("FILE_DAMAGE_ERROR_MSG");
        } else {
          pageInfo = getPageInfo.getTruePageInfo(pageCount);
        }
      } else {
        pageInfo = getPageInfo.getFalsePageInfo("FILE_DAMAGE_ERROR_MSG");
      }
    } catch (Exception e) {
      pageInfo = getPageInfo.getFalsePageInfo("FILE_DAMAGE_ERROR_MSG");
    }
    return pageInfo;
  }

  private PageInfo getPdfPageInfo(byte[] data, String filePath) {
    try {
      if (!parameterCalibration.checkIsEncrypt(data, filePath)) {
        // 获得PDF总页码
        com.aspose.pdf.Document pdf = getDocumentObject.getPdf(data);
        int pageCount = pdf.getPages().size();
        if (pageCount == 0) {
          pageInfo = getPageInfo.getFalsePageInfo("FILE_DAMAGE_ERROR_MSG");
        } else {
          pageInfo = getPageInfo.getTruePageInfo(pageCount);
        }
        pdf.close();
      } else {
        pageInfo = getPageInfo.getFalsePageInfo("FILE_DAMAGE_ERROR_MSG");
      }
    } catch (Exception e) {
      pageInfo = getPageInfo.getFalsePageInfo("FILE_DAMAGE_ERROR_MSG");
    }
    return pageInfo;
  }
}
