package com.facishare.document.preview.convert.office.controller;


import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.constant.Office2PdfException;
import com.facishare.document.preview.convert.office.domain.ConvertResult;
import com.facishare.document.preview.convert.office.domain.PageInfo;
import com.facishare.document.preview.convert.office.utils.ExcelUtil;
import com.facishare.document.preview.convert.office.utils.ParamCheckUtil;
import com.facishare.document.preview.convert.office.utils.PdfUtil;
import com.facishare.document.preview.convert.office.utils.PptUtil;
import com.facishare.document.preview.convert.office.utils.ResponseUtil;
import com.facishare.document.preview.convert.office.utils.WordUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/Api/Office/")
public class ApiController {

  @PostMapping("/GetPageInfoByStream")
  public PageInfo getDocumentMetaInfo(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) {
    // 校验Path并返回文件类型
    FileTypeEnum fileType = ParamCheckUtil.getFileType(path);
    // 校验文件大小并返回文件字节流
    byte[] fileBytes = ParamCheckUtil.getFileBytes(file);
    return switch (fileType) {
      case DOC, DOCX -> WordUtil.getWordPageCount(fileBytes);
      case XLS, XLSX -> ExcelUtil.getExcelPageCount(fileBytes);
      case PPT, PPTX -> PptUtil.getPptPageCount(fileBytes);
      default -> PdfUtil.getPdfPageCount(fileBytes);
    };
  }

  @PostMapping(value = "/ConvertExcel2HtmlByStream")
  public ConvertResult convertExcel2HtmlByStream(@RequestParam("path") String path,
      @RequestParam("page") int page,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    ParamCheckUtil.isExcelType(path);
    ResponseUtil.setResponse(response);
    try (InputStream inputStream = file.getInputStream()) {
      try (OutputStream outputStream = response.getOutputStream()) {
        outputStream.write(ExcelUtil.ExcelToHtml(inputStream, page));
        return null;
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.RESPONSE_STREAM_ERROR, e);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_STREAM_ERROR, e);
    }
  }

  @PostMapping(value = "/ConvertFileByStream")
  public String convertFileByStream(@RequestParam("path") String path,
      @RequestParam("file") MultipartFile file, HttpServletResponse response) {
    FileTypeEnum fileType = ParamCheckUtil.getFileType(path);
    byte[] fileBytes = ParamCheckUtil.getFileBytes(file);
    ResponseUtil.setResponse(response);
    try (OutputStream outputStream = response.getOutputStream()) {
      switch (fileType) {
        case DOC, DOCX -> outputStream.write(WordUtil.convertDocToDocx(fileBytes));
        case PPT, PPTX -> outputStream.write(PptUtil.convertPptToPptx(fileBytes));
        case XLS, XLSX -> outputStream.write(ExcelUtil.convertXlsToXlsx(fileBytes));
        default -> throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.RESPONSE_STREAM_ERROR, e);
    }
    return null;
  }

  @PostMapping(value = "/ConvertOnePageOffice2PdfByStream")
  public String convertOnePageOffice2PdfByStream(String path, int page, @RequestParam("file") MultipartFile file, HttpServletResponse response){
    FileTypeEnum fileType = ParamCheckUtil.getFileType(path);
    ResponseUtil.setResponse(response);
    //转pdf，只有Word文档下标从0开始
    try (InputStream fileInputStream=file.getInputStream()){
      try (OutputStream outputStream=response.getOutputStream()) {
        switch (fileType) {
          case DOC, DOCX -> outputStream.write(WordUtil.convertWordOnePageToPdf(fileInputStream, page));
          case PPT, PPTX -> outputStream.write(PptUtil.convertPptOnePageToPdf(fileInputStream, page));
          default -> throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
        }
        return null;
      } catch (IOException e) {
        throw new Office2PdfException(ErrorInfoEnum.RESPONSE_STREAM_ERROR, e);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_STREAM_ERROR, e);
    }
  }

  @PostMapping(value = "/ConvertOffice2PdfByStream")
  public String convertOffice2PdfByStream(@RequestParam("path") String path, @RequestParam("file") MultipartFile file, HttpServletResponse response){
    FileTypeEnum fileType = ParamCheckUtil.getFileType(path);
    ResponseUtil.setResponse(response);
    try (InputStream fileInputStream=file.getInputStream()){
      try (OutputStream outputStream=response.getOutputStream()) {
        switch (fileType) {
          case DOC, DOCX -> outputStream.write(WordUtil.convertDocToPdf(fileInputStream));
          case PPT, PPTX -> outputStream.write(PptUtil.convertPptToPdf(fileInputStream));
          default -> throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
        }
        return null;
      } catch (IOException e) {
        throw new Office2PdfException(ErrorInfoEnum.RESPONSE_STREAM_ERROR, e);
      }
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_STREAM_ERROR, e);
    }
  }
}
