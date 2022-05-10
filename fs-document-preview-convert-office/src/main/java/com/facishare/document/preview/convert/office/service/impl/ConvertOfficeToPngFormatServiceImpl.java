package com.facishare.document.preview.convert.office.service.impl;

import com.aspose.pdf.devices.PngDevice;
import com.aspose.pdf.devices.Resolution;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.PageSet;
import com.aspose.words.SaveFormat;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;
import com.facishare.document.preview.convert.office.service.ConvertOfficeToPngFormatService;
import com.facishare.document.preview.convert.office.utils.FileProcessingUtil;
import com.facishare.document.preview.convert.office.utils.InitializeAsposePdfUtil;
import com.facishare.document.preview.convert.office.utils.InitializeAsposePptUtil;
import com.facishare.document.preview.convert.office.utils.InitializeAsposeWordUtil;
import com.github.autoconf.ConfigFactory;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [将Office文档转换为PNG格式]
 * @createTime : [2022/5/10 11:29]
 * @updateUser : [Andy]
 * @updateTime : [2022/5/10 11:29]
 * @updateRemark : [拆分转换为Png格式的服务为单独的一个服务]
 */
@Service
public class ConvertOfficeToPngFormatServiceImpl implements ConvertOfficeToPngFormatService {

  private String office2PngTempPath;
  private String office2PngZipTempPath;
  private int office2pngConvertPptToPngWidth;
  private int office2pngConvertPptToPngHeight;
  private int office2pngConvertPdfToPngDpi;

  @PostConstruct
  public void init() {
    ConfigFactory.getConfig("fs-dps-office2pdf", config -> {
      office2PngTempPath = config.get("office2PngTempPath");
      office2PngZipTempPath = config.get("office2PngZipTempPath");
      office2pngConvertPptToPngWidth = config.getInt("office2pngConvertPptToPngWidth");
      office2pngConvertPptToPngHeight = config.getInt("office2pngConvertPptToPngHeight");
      office2pngConvertPdfToPngDpi = config.getInt("office2pngConvertPdfToPngDpi");
    });
  }

  @Override
  public byte[] convertDocumentAllPageToPng(InputStream file, FileTypeEnum fileType) {
    switch (fileType) {
      case DOC:
      case DOCX:
        return convertWordAllPageToPng(file);
      case PPT:
      case PPTX:
        return convertPptAllPageToPng(file);
      case PDF:
        return convertPdfAllPageToPng(file);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }

  public byte[] convertWordAllPageToPng(InputStream file) {
    office2PngTempPath = FileProcessingUtil.createDirectory(office2PngTempPath);
    office2PngZipTempPath = FileProcessingUtil.createDirectory(office2PngZipTempPath);
      Document doc = InitializeAsposeWordUtil.getWord(file);
      try {
        int pageCount = doc.getPageCount();
        for (int i = 0; i < pageCount; i++) {
          String fileName = office2PngTempPath + "\\" + i + ".png";
          ImageSaveOptions imageOptions = new ImageSaveOptions(com.aspose.words.SaveFormat.PNG);
          imageOptions.setUseHighQualityRendering(true);
          imageOptions.setPageSet(new com.aspose.words.PageSet(i));
          try {
            doc.save(fileName, imageOptions);
          } catch (Exception e) {
            throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_PNG_FAILURE, e);
          }
        }
      }catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.WORD_PAGE_NUMBER_PARAMETER_ZERO, e);
      }
    return FileProcessingUtil.getZipByte(office2PngTempPath, office2PngZipTempPath);
  }

  public byte[] convertPptAllPageToPng(InputStream file) {
    office2PngTempPath = FileProcessingUtil.createDirectory(office2PngTempPath);
    office2PngZipTempPath = FileProcessingUtil.createDirectory(office2PngZipTempPath);
      Presentation ppt = InitializeAsposePptUtil.getPpt(file);
      BufferedImage bufferedImage=null;
      try{
        for (com.aspose.slides.ISlide slide : ppt.getSlides()) {
          Dimension size = new Dimension(office2pngConvertPptToPngWidth, office2pngConvertPptToPngHeight);
          //设置生成图片的大小
          bufferedImage = slide.getThumbnail(size);
          File outputFile = new File(office2PngTempPath + "\\" + slide.getSlideNumber() + ".png");
          ImageIO.write(bufferedImage, "PNG", outputFile);
        }
      } catch (IOException e) {
        throw new Office2PdfException(ErrorInfoEnum.WORD_FILE_SAVING_PNG_FAILURE, e);
      } finally {
        if (bufferedImage!=null){
          bufferedImage.getGraphics().dispose();
        }
        ppt.dispose();
      }
      return FileProcessingUtil.getZipByte(office2PngTempPath, office2PngZipTempPath);
  }

  public byte[] convertPdfAllPageToPng(InputStream file) {
    office2PngTempPath = FileProcessingUtil.createDirectory(office2PngTempPath);
    office2PngZipTempPath = FileProcessingUtil.createDirectory(office2PngZipTempPath);
      com.aspose.pdf.Document pdf = InitializeAsposePdfUtil.getPdf(file);
      int pageCount = pdf.getPages().size();
      Resolution imageResolution = new Resolution(office2pngConvertPdfToPngDpi);
      PngDevice rendererPng = new PngDevice(imageResolution);
      try {
        for (int i = 1; i <= pageCount; i++) {
          rendererPng.process(pdf.getPages().get_Item(i), Files.newOutputStream(Paths.get(office2PngTempPath + "\\" + i + ".png")));
        }
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.PDF_FILE_SAVING_PNG_FAILURE, e);
      }finally {
        pdf.close();
      }
    return FileProcessingUtil.getZipByte(office2PngTempPath, office2PngZipTempPath);
  }


  @Override
  public byte[] convertDocumentOnePageToPng(InputStream file, FileTypeEnum fileType, int page ) {
    switch (fileType) {
      case DOC:
      case DOCX:
        return convertWordOnePageToPng(file, page);
      case PPT:
      case PPTX:
        return convertPptOnePageToPng(file, page);
      case PDF:
        return convertPdfOnePageToPng(file, page + 1);
      default:
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }
  private byte[] convertWordOnePageToPng(InputStream file,int page){
      Document doc =InitializeAsposeWordUtil.getWord(file);
      ImageSaveOptions imageOptions = new ImageSaveOptions(SaveFormat.PNG);
      imageOptions.setUseHighQualityRendering(true);
      imageOptions.setPageSet(new PageSet(page));
      try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
        doc.save(outputStream, imageOptions);
        return outputStream.toByteArray();
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
      }finally {
        doc=null;
      }
  }
  private byte[] convertPptOnePageToPng(InputStream file,int page){
      Presentation ppt = InitializeAsposePptUtil.getPpt(file);
      Dimension size = new Dimension(office2pngConvertPptToPngWidth, office2pngConvertPptToPngHeight);
      com.aspose.slides.ISlide slide = ppt.getSlides().get_Item(page);
      BufferedImage bufferedImage = slide.getThumbnail(size);
      try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
        ImageIO.write(bufferedImage, "PNG", outputStream);
        return outputStream.toByteArray();
      } catch (IOException e) {
        throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
      } finally {
        bufferedImage.getGraphics().dispose();
        ppt.dispose();
      }
  }
  private byte[] convertPdfOnePageToPng(InputStream file,int page){
      com.aspose.pdf.Document pdf = InitializeAsposePdfUtil.getPdf(file);
      com.aspose.pdf.devices.Resolution imageResolution = new com.aspose.pdf.devices.Resolution(office2pngConvertPdfToPngDpi);
      com.aspose.pdf.devices.PngDevice rendererPng = new com.aspose.pdf.devices.PngDevice(imageResolution);
      try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
        rendererPng.process(pdf.getPages().get_Item(page), outputStream);
        return outputStream.toByteArray();
      } catch (Exception e) {
        throw new Office2PdfException(ErrorInfoEnum.PAGE_NUMBER_PARAMETER_ERROR, e);
      } finally {
        pdf.close();
      }
  }
}
