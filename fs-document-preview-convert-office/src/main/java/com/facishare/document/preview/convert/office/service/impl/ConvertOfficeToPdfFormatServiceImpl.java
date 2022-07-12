package com.facishare.document.preview.convert.office.service.impl;

import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.aspose.words.FontSettings;
import com.aspose.words.LoadOptions;
import com.aspose.words.PdfSaveOptions;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.facishare.document.preview.convert.office.service.ConvertOfficeToPdfFormatService;
import com.facishare.document.preview.convert.office.utils.InitializeAsposePptUtil;
import com.facishare.document.preview.convert.office.utils.InitializeAsposeWordUtil;
import com.github.autoconf.ConfigFactory;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [将Office文档转换为PDF格式]
 * @createTime : [2022/5/10 11:28]
 * @updateUser : [Andy]
 * @updateTime : [2022/5/10 11:28]
 * @updateRemark : [将转换文档为PDF格式的服务单独拆分为一个服务]
 */
@Service
public class ConvertOfficeToPdfFormatServiceImpl implements ConvertOfficeToPdfFormatService {


  private String office2PngWordDocumentEncoding;
  private String office2PdfWordDocumentFontsDirectory;

  @PostConstruct
  public void init() {
    ConfigFactory.getConfig("fs-dps-office2pdf", config -> {
      office2PngWordDocumentEncoding = config.get("office2PngWordDocumentEncoding");
      office2PdfWordDocumentFontsDirectory = config.get("office2PdfWordDocumentFontsDirectory");
    });
  }

  @Override
  public byte[] convertAllPageWordOrPptToPdf(InputStream file, FileTypeEnum fileType) {
    switch (fileType) {
      case DOC:
      case DOCX:
        return convertDocToPdf(file);
      case PPT:
      case PPTX:
        return convertPptToPdf(file);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }

  private byte[] convertDocToPdf(InputStream file) {
    LoadOptions options = new LoadOptions();
    options.setEncoding(Charset.forName(office2PngWordDocumentEncoding));
    Document doc = InitializeAsposeWordUtil.getWord(file, options);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      PdfSaveOptions pdfSaveOptions = new com.aspose.words.PdfSaveOptions();
      //不嵌入任何字体
      pdfSaveOptions.setFontEmbeddingMode(2);
      pdfSaveOptions.setZoomBehavior(0);
      FontSettings.getDefaultInstance().setFontsFolder(office2PdfWordDocumentFontsDirectory, true);
      doc.save(outputStream,pdfSaveOptions);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_FAILURE, e);
    }
  }

  private byte[] convertPptToPdf(InputStream file) {
    Presentation ppt = InitializeAsposePptUtil.getPpt(file);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ppt.save(outputStream, com.aspose.slides.SaveFormat.Pdf);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PPT_FILE_SAVING_FAILURE, e);
    } finally {
      ppt.dispose();
    }
  }

  @Override
  public byte[] convertDocumentOnePageToPdf(InputStream file, FileTypeEnum fileType, int page) {
    switch (fileType) {
      case DOC:
      case DOCX:
        return convertWordOnePageToPdf(file, page - 1);
      case PPT:
      case PPTX:
        return convertPptOnePageToPdf(file, page);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }

  private byte[] convertWordOnePageToPdf(InputStream file, int page) {
    //设置Word文件的默认编码
    LoadOptions options = new LoadOptions();
    options.setEncoding(Charset.forName(office2PngWordDocumentEncoding));
    Document doc = InitializeAsposeWordUtil.getWord(file, options);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      PdfSaveOptions pdfSaveOptions = new com.aspose.words.PdfSaveOptions();
      pdfSaveOptions.setPageSet(new com.aspose.words.PageSet(page));
      //0 嵌入所有字体 1 嵌入了除标准Windows字体Arial和Times New Roman之外的所有字体  2 不嵌入任何字体。
      pdfSaveOptions.setFontEmbeddingMode(2);
      //0 文档的显示方式留给 PDF 查看器。通常，查看器会显示适合页面宽度的文档。 1 使用指定的缩放系数显示页面。 2 显示页面，使其完全可见。 3 适合页面的宽度。 4 适合页面的高度。 5 适合边界框（包含页面上所有可见元素的矩形）。
      pdfSaveOptions.setZoomBehavior(0);
      FontSettings.getDefaultInstance().setFontsFolder(office2PdfWordDocumentFontsDirectory, true);
      doc.save(outputStream, pdfSaveOptions);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    } finally {
      doc = null;
    }
  }

  private byte[] convertPptOnePageToPdf(InputStream file, int page) {
    Presentation ppt = InitializeAsposePptUtil.getPpt(file);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      int[] page2 = {page};
      ppt.save(outputStream, page2, com.aspose.slides.SaveFormat.Pdf);
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
    } finally {
      ppt.dispose();
    }
  }

}
