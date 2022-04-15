package com.facishare.document.preview.convert.office.service.impl;

import com.facishare.document.preview.convert.office.model.ConvertResultInfo;
import com.facishare.document.preview.convert.office.service.ConvertDocumentFormatService;
import com.facishare.document.preview.convert.office.utils.ConvertDocument;
import com.facishare.document.preview.convert.office.utils.GetConvertResultInfo;
import com.facishare.document.preview.convert.office.utils.ParameterCalibration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Andy
 */
@Slf4j
@Service
public class ConvertDocumentFormatServiceImpl implements ConvertDocumentFormatService {

  public ConvertResultInfo convertOnePageExcelToHtml(byte[] data, int page) {
    return new ConvertDocument().getHtml(data, page);
  }

  public ConvertResultInfo convertAllPageWordOrPptToPdf(byte[] data, String fileName) {
    String fileSuffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    switch (fileSuffix) {
      case ".doc":
      case ".docx":
        return new ConvertDocument().convertWordToPdf(data);
      case ".ppt":
      case ".pptx":
        return new ConvertDocument().convertPptToPdf(data);
      default: {
        return new GetConvertResultInfo().getFalseConvertResultInfo("参数不是doc、docx、ppt、pptx的任意一种!" + fileSuffix);
      }
    }
  }

  public ConvertResultInfo convertDocumentSuffix(byte[] data, String fileName) {
    try {
      if (!ParameterCalibration.checkIsEncrypt(data, fileName)) {
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        switch (fileSuffix) {
          case ".doc":
          case ".docx":
            return new ConvertDocument().convertDocToDocx(data);
          case ".ppt":
          case ".pptx":
            return new ConvertDocument().convertPptToPptx(data);
          case ".xls":
            return new ConvertDocument().convertXlsToXlsx(data);
          default:
            return new GetConvertResultInfo().getTrueConvertResultInfo(data);
        }
      }
    } catch (Exception e) {
      log.error(e.toString());
      return new GetConvertResultInfo().getTrueConvertResultInfo(data);
    }
    return new GetConvertResultInfo().getTrueConvertResultInfo(data);
  }

  public ConvertResultInfo convertDocumentAllPageToPng(byte[] data, String fileName) {
    String fileSuffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    switch (fileSuffix) {
      case ".doc":
      case ".docx":
        return new ConvertDocument().convertWordAllPageToPng(data);
      case ".ppt":
      case ".pptx":
        return new ConvertDocument().convertPptAllPageToPng(data);
      case ".pdf":
        return new ConvertDocument().convertPdfAllPageToPng(data);
      default: {
        return new GetConvertResultInfo().getFalseConvertResultInfo("params error,need doc or ppt or pdf,but now" + fileSuffix);
      }
    }
  }

  public ConvertResultInfo convertDocumentOnePageToPng(byte[] data, String fileName, int page) {
    String fileSuffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    switch (fileSuffix) {
      case ".doc":
      case ".docx":
        return new ConvertDocument().convertWordOnePageToPng(data, page);
      case ".ppt":
      case ".pptx":
        return new ConvertDocument().convertPptOnePageToPng(data, page);
      case ".pdf":
        return new ConvertDocument().convertPdfOnePageToPng(data, page);
      default: {
        return new GetConvertResultInfo().getFalseConvertResultInfo("params error,need doc or ppt or pdf,but now" + fileSuffix);
      }
    }
  }

  @Override
  public ConvertResultInfo convertDocumentOnePageToPdf(byte[] data, String fileName, int page) {
    String fileSuffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    switch (fileSuffix) {
      case ".doc":
      case ".docx":
        return new ConvertDocument().convertWordOnePageToPdf(data, page);
      case ".ppt":
      case ".pptx":
        return new ConvertDocument().convertPptOnePageToPdf(data, page);
      default: {
        return new GetConvertResultInfo().getFalseConvertResultInfo("params error,need doc or ppt ,but now" + fileSuffix);
      }
    }
  }
}
























































