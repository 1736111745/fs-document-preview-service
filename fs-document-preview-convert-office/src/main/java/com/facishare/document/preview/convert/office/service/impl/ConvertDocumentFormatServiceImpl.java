package com.facishare.document.preview.convert.office.service.impl;

import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.facishare.document.preview.convert.office.service.ConvertDocumentFormatService;
import com.facishare.document.preview.convert.office.utils.ExcelObjectUtil;
import com.facishare.document.preview.convert.office.utils.PdfObjectUtil;
import com.facishare.document.preview.convert.office.utils.PptObjectUtil;
import com.facishare.document.preview.convert.office.utils.WordObjectUtil;
import com.github.autoconf.ConfigFactory;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Andy
 */
@Service
public class ConvertDocumentFormatServiceImpl implements ConvertDocumentFormatService {

  private String office2PngTempPath;
  private String office2PngZipTempPath;

  @PostConstruct
  public void init() {
    ConfigFactory.getConfig("fs-dps-office2pdf", config -> {
      office2PngTempPath = config.get("office2PngTempPath");
      office2PngZipTempPath = config.get("office2PngZipTempPath");
    });
  }

  public ByteArrayOutputStream convertOnePageExcelToHtml(MultipartFile file, int page,
      ByteArrayOutputStream fileOutputStream) {
    return ExcelObjectUtil.getHtml(file, page, fileOutputStream);
  }

  public ByteArrayOutputStream convertAllPageWordOrPptToPdf(MultipartFile file,
      FileTypeEnum fileType, ByteArrayOutputStream fileOutputStream) {
    switch (fileType) {
      case DOC:
      case DOCX:
        return WordObjectUtil.convertDocToPdf(file, fileOutputStream);
      case PPT:
      case PPTX:
        return PptObjectUtil.convertPptToPdf(file, fileOutputStream);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }

  public ByteArrayOutputStream convertDocumentSuffix(MultipartFile file, FileTypeEnum fileType,
      ByteArrayOutputStream fileOutputStream) {
    switch (fileType) {
      case DOC:
      case DOCX:
        return WordObjectUtil.convertDocToDocx(file, fileOutputStream);
      case PPT:
      case PPTX:
        return PptObjectUtil.convertPptToPptx(file, fileOutputStream);
      case XLS:
      case XLSX:
        return ExcelObjectUtil.convertXlsToXlsx(file, fileOutputStream);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }

  public byte[] convertDocumentAllPageToPng(MultipartFile file, FileTypeEnum fileType) {
    switch (fileType) {
      case DOC:
      case DOCX:
        return WordObjectUtil.convertWordAllPageToPng(file, office2PngTempPath,
            office2PngZipTempPath);
      case PPT:
      case PPTX:
        return PptObjectUtil.convertPptAllPageToPng(file, office2PngTempPath,
            office2PngZipTempPath);
      case PDF:
        return PdfObjectUtil.convertPdfAllPageToPng(file, office2PngTempPath,
            office2PngZipTempPath);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }

  public  ByteArrayOutputStream  convertDocumentOnePageToPng(MultipartFile file, FileTypeEnum fileType, int page,ByteArrayOutputStream fileOutputStream) {
    switch (fileType) {
      case DOC:
      case DOCX: return WordObjectUtil.convertWordOnePageToPng(file, page,fileOutputStream);
      case PPT:
      case PPTX:
        return PptObjectUtil.convertPptOnePageToPng(file, page,fileOutputStream);
      case PDF:
        return PdfObjectUtil.convertPdfOnePageToPng(file, page+1,fileOutputStream);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }

  public ByteArrayOutputStream  convertDocumentOnePageToPdf(MultipartFile file, FileTypeEnum fileType, int page,ByteArrayOutputStream fileOutputStream) {
    switch (fileType) {
      case DOC:
      case DOCX: return WordObjectUtil.convertWordOnePageToPdf(file, page - 1,fileOutputStream);
      case PPT:
      case PPTX: return PptObjectUtil.convertPptOnePageToPdf(file, page,fileOutputStream);
      default: throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }
}
























































