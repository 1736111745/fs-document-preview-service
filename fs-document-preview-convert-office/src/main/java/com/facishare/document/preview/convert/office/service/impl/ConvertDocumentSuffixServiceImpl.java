package com.facishare.document.preview.convert.office.service.impl;

import com.aspose.cells.Workbook;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.facishare.document.preview.convert.office.service.ConvertDocumentSuffixService;
import com.facishare.document.preview.convert.office.utils.InitializeAsposeExcelUtil;
import com.facishare.document.preview.convert.office.utils.InitializeAsposePptUtil;
import com.facishare.document.preview.convert.office.utils.InitializeAsposeWordUtil;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.springframework.stereotype.Service;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [转换Office文档为最新格式 如 xls -> xlsx]
 * @createTime : [2022/5/10 11:30]
 * @updateUser : [Andy]
 * @updateTime : [2022/5/10 11:30]
 * @updateRemark : [将转换Office文档后缀名的服务单独拆分为一个服务]
 */
@Service
public class ConvertDocumentSuffixServiceImpl implements ConvertDocumentSuffixService {

  @Override
  public byte[] convertDocumentSuffix(InputStream file, FileTypeEnum fileType) {
    switch (fileType) {
      case DOC:
      case DOCX:
        return convertDocToDocx(file);
      case PPT:
      case PPTX:
        return convertPptToPptx(file);
      case XLS:
      case XLSX:
        return convertXlsToXlsx(file);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }
  private byte[] convertDocToDocx(InputStream file) {
      Document doc = InitializeAsposeWordUtil.getWord(file);
      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        doc.save(outputStream, com.aspose.words.SaveFormat.DOCX);
        return outputStream.toByteArray();
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_FAILURE, e);
      } finally {
        doc = null;
      }
  }

  private byte[] convertPptToPptx(InputStream file) {
      Presentation ppt = InitializeAsposePptUtil.getPpt(file);
      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        ppt.save(outputStream, com.aspose.slides.SaveFormat.Pptx);
        return outputStream.toByteArray();
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.PPT_FILE_SAVING_FAILURE, e);
      } finally {
        ppt.dispose();
      }
  }

  private byte[] convertXlsToXlsx(InputStream file) {
      Workbook workbook =  InitializeAsposeExcelUtil.getWorkBook(file);
      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        workbook.save(outputStream, com.aspose.cells.SaveFormat.XLSX);
        return outputStream.toByteArray();
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.EXCEL_FILE_SAVING_FAILURE, e);
      } finally {
        workbook.dispose();
      }
  }

}
