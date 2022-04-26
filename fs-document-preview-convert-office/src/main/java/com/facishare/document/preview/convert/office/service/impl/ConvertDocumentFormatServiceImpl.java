package com.facishare.document.preview.convert.office.service.impl;

import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.facishare.document.preview.convert.office.service.ConvertDocumentFormatService;
import com.facishare.document.preview.convert.office.utils.ExcelObjectUtil;
import com.facishare.document.preview.convert.office.utils.PptObjectUtil;
import com.facishare.document.preview.convert.office.utils.WordObjectUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Andy
 */
@Service
public class ConvertDocumentFormatServiceImpl implements ConvertDocumentFormatService {

  @Value("${office2PngTempPath}")
  private String office2PngTempPath;

  @Value("${office2PngZipTempPath}")
  private String office2PngZipTempPath;

  public byte[] convertOnePageExcelToHtml(byte[] data, int page) {
    return new ExcelObjectUtil().getHtml(data, page);
  }

  public byte[] convertAllPageWordOrPptToPdf(byte[] fileBate, FileTypeEnum fileType) {
    switch (fileType) {
      case DOC:
      case DOCX:
        return WordObjectUtil.convertDocToPdf(fileBate);
      case PPT:
      case PPTX:
        return PptObjectUtil.convertPptToPdf(fileBate);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }

  public byte[] convertDocumentSuffix(byte[] fileBate, FileTypeEnum fileType) {
    switch (fileType) {
      case DOC:
      case DOCX:
        return WordObjectUtil.convertDocToDocx(fileBate);
      case PPT:
      case PPTX:
        return PptObjectUtil.convertPptToPptx(fileBate);
      case XLS:
      case XLSX:
        return ExcelObjectUtil.convertXlsToXlsx(fileBate);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }

  public byte[] convertDocumentAllPageToPng(byte[] fileBate, FileTypeEnum fileType) {
    switch (fileType) {
      case DOC:
      case DOCX:
        return WordObjectUtil.convertWordAllPageToPng(fileBate, office2PngTempPath, office2PngZipTempPath);
      case PPT:
      case PPTX:
        return PptObjectUtil.convertPptAllPageToPng(fileBate, office2PngTempPath, office2PngZipTempPath);
      case PDF:
        return ExcelObjectUtil.convertPdfAllPageToPng(fileBate, office2PngTempPath, office2PngZipTempPath);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }

  public byte[] convertDocumentOnePageToPng(byte[] data, FileTypeEnum fileType, int page) {
    return new byte[0];
  }

  public byte[] convertDocumentOnePageToPdf(byte[] bytes, FileTypeEnum fileType, int page) {
    return new byte[0];
  }

}
























































