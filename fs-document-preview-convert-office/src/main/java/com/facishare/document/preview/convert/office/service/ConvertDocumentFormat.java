package com.facishare.document.preview.convert.office.service;

import com.aspose.cells.License;
import com.facishare.document.preview.convert.office.model.ConvertResultInfo;
import com.facishare.document.preview.convert.office.utils.ConvertDocument;
import com.facishare.document.preview.convert.office.utils.GetConvertResultInfo;
import com.facishare.document.preview.convert.office.utils.ParameterCalibration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * @author Andy
 */
@Slf4j
@Service
public class ConvertDocumentFormat {

  @Resource
  private ConvertDocument convertDocument;

  @Resource
  private GetConvertResultInfo getConvertResultInfo;

  @Resource
  private ParameterCalibration parameterCalibration;

  public static boolean getLicense() {
    try {
      InputStream is = ConvertDocumentFormat.class.getClassLoader().getResourceAsStream("license.xml");
      License aposeLic = new License();
      aposeLic.setLicense(is);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public ConvertResultInfo convertOnePageExcelToHtml(byte[] data, int page) {
    return convertDocument.getHtml(data, page);
  }

  public ConvertResultInfo convertAllPageWordOrPptToPdf(byte[] data, String fileName) {
    String fileSuffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    switch (fileSuffix) {
      case ".doc":
      case ".docx":
        return convertDocument.convertWordToPdf(data);
      case ".ppt":
      case ".pptx":
        return convertDocument.convertPptToPdf(data);
      default: {
        return getConvertResultInfo.getFalseConvertResultInfo("参数不是doc、docx、ppt、pptx的任意一种!" + fileSuffix);
      }
    }
  }

  public ConvertResultInfo convertDocumentSuffix(byte[] data, String fileName) {
    try {
      if (!parameterCalibration.checkIsEncrypt(data, fileName)) {
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        switch (fileSuffix) {
          case ".doc":
          case ".docx":
            return convertDocument.convertDocToDocx(data);
          case ".ppt":
          case ".pptx":
            return convertDocument.convertPptToPptx(data);
          case ".xls":
            return convertDocument.convertXlsToXlsx(data);
          default:
            return getConvertResultInfo.getTrueConvertResultInfo(data);
        }
      }
    } catch (Exception e) {
      log.error(e.toString());
      return getConvertResultInfo.getTrueConvertResultInfo(data);
    }
    return getConvertResultInfo.getTrueConvertResultInfo(data);
  }

  public ConvertResultInfo convertDocumentAllPageToPng(byte[] data, String fileName) {
    String fileSuffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    switch (fileSuffix) {
      case ".doc":
      case ".docx":
        return convertDocument.convertWordAllPageToPng(data);
      case ".ppt":
      case ".pptx":
        return convertDocument.convertPptAllPageToPng(data);
      case ".pdf":
        return convertDocument.convertPdfAllPageToPng(data);
      default: {
        return getConvertResultInfo.getFalseConvertResultInfo("params error,need doc or ppt or pdf,but now" + fileSuffix);
      }
    }
  }

  public ConvertResultInfo convertDocumentOnePageToPng(byte[] data, String fileName,int page) {
    String fileSuffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    switch (fileSuffix) {
      case ".doc":
      case ".docx":
        return convertDocument.convertWordOnePageToPng(data,page);
      case ".ppt":
      case ".pptx":
        return convertDocument.convertPptOnePageToPng(data,page);
      case ".pdf":
        return convertDocument.convertPdfOnePageToPng(data,page);
      default: {
        return getConvertResultInfo.getFalseConvertResultInfo("params error,need doc or ppt or pdf,but now" + fileSuffix);
      }
    }
  }
}
























































