package com.facishare.document.preview.convert.office.utils;

import cn.hutool.core.util.ZipUtil;
import com.aspose.cells.HtmlSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.aspose.slides.ISlide;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.PageSet;
import com.aspose.words.PdfSaveOptions;
import com.facishare.document.preview.convert.office.model.ConvertResultInfo;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author Andy
 */
@Slf4j
public class ConvertDocument {

  private static byte[] toByteArray(InputStream in) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024 * 4];
    int n = 0;
    try {
      while ((n = in.read(buffer)) != -1) {
        out.write(buffer, 0, n);
      }
    } catch (IOException e) {

    }
    return out.toByteArray();
  }

  private static void deleteTempDirectory(File file) {
    File[] listFile = file.listFiles();
    if (listFile != null) {
      for (File temp : listFile) {
        deleteTempDirectory(temp);
      }
    }
    file.delete();
  }

  public ConvertResultInfo getHtml(byte[] data, int filepage) {
    int page=filepage-1;
    ConvertResultInfo convertResultInfo = new ConvertResultInfo();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    try {
      Workbook workbook = new Workbook(fileInputStream);
      WorksheetCollection worksheetCollection = workbook.getWorksheets();
      Worksheet worksheet = worksheetCollection.get(page);
      int pageCount = worksheetCollection.getCount();

      if (page >= pageCount || page < 0) {
        convertResultInfo.setErrorMsg("页码参数错误");
      } else {
        //获取最大行数
        int rows = worksheet.getCells().getMaxRow();
        //获取有数据的行数 并+10
        int validRows = worksheet.getCells().getMaxDataRow() + 10;
        // 有数据行数与最大行数比较，取较小的值
        validRows = Math.min(validRows, rows);
        // 有数据行数与5000相比较，取较小的值
        validRows = Math.min(validRows, 5000);
        if (validRows > 0) {
          int blankRowStart = validRows + 1;
          blankRowStart = Math.max(blankRowStart, 0);
          int blankRowEnd = rows - blankRowStart;
          blankRowEnd = Math.max(blankRowEnd, 0);
          if (blankRowEnd > 0) {
            // 删除表中多行 blankRowsStart要删除的第一行索引 blankRowEnd要删除的行数
            worksheet.getCells().deleteRows(blankRowStart, blankRowEnd, true);
          }
          // 总列数
          int columns = worksheet.getCells().getMaxDisplayRange().getColumnCount();
          // 有效列数
          int validColumns = worksheet.getCells().getMaxDataColumn();
          validColumns = validColumns > 1000 ? 1000 : validColumns;
          if (validColumns > 0 && worksheet.getCells().getMaxColumn() < 16382) {
            int blankColumStart = validColumns + 1;
            blankColumStart = blankRowStart < 0 ? 0 : blankColumStart;
            int blankColumEnd = columns - blankColumStart;
            blankColumEnd = blankColumEnd < 0 ? 0 : blankRowEnd;
          }
          if (blankRowEnd > 0) {
            worksheet.getCells().deleteColumns(blankRowStart, blankRowEnd, true);
          }
          com.aspose.cells.Cells cells = worksheet.getCells();
          for (int col = 0; col < validColumns; col++) {
            //cells.getColumnWidthPixel()获取单元格列的像素
            cells.setColumnWidthPixel(col, (int) (cells.getColumnWidthPixel(col) * 2f));
          }
          // 设置当前活动单元格的索引
          worksheetCollection.setActiveSheetIndex(page);
          HtmlSaveOptions saveOptions = new com.aspose.cells.HtmlSaveOptions();
          // 默认为 hidden 隐藏 即 this.p=0
          saveOptions.setHiddenColDisplayType(0);
          saveOptions.setHiddenRowDisplayType(0);
          saveOptions.getImageOptions().setCellAutoFit(true);
          saveOptions.setExportImagesAsBase64(true);
          saveOptions.setCreateDirectory(true);
          saveOptions.setEnableHTTPCompression(true);
          saveOptions.setExportActiveWorksheetOnly(true);
          // 将文件输出流与定义的HTML文件格式绑定
          workbook.save(fileOutputStream, saveOptions);
          workbook.dispose();
          convertResultInfo.setSuccess(true);
          convertResultInfo.setBytes(fileOutputStream.toByteArray());
        }
      }
    } catch (Exception e) {
      convertResultInfo.setErrorMsg(e.toString());
    } finally {
      try {
        fileInputStream.close();
        fileOutputStream.close();
      } catch (IOException e) {
        convertResultInfo.setErrorMsg(e.toString());
      }
    }
    return convertResultInfo;
  }

  public ConvertResultInfo convertWordToPdf(byte[] data) {
    Document doc = new GetWordsDocumentObject().getWord(data);
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    try {
      doc.save(fileOutputStream, com.aspose.words.SaveFormat.PDF);
    } catch (Exception e) {
      return new GetConvertResultInfo().getFalseConvertResultInfo(e.toString());
    }
    return new GetConvertResultInfo().getTrueConvertResultInfo(fileOutputStream);
  }

  public ConvertResultInfo convertPptToPdf(byte[] data) {
    Presentation ppt = new GetPptDocumentObject().getPpt(data);
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    ppt.save(fileOutputStream, com.aspose.slides.SaveFormat.Pdf);
    return new GetConvertResultInfo().getTrueConvertResultInfo(fileOutputStream);
  }

  public ConvertResultInfo convertDocToDocx(byte[] data) {
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    Document doc = new GetWordsDocumentObject().getWord(data);
    try {
      doc.save(fileOutputStream, com.aspose.words.SaveFormat.DOCX);
    } catch (Exception e) {
      log.error(e.toString());
      return new GetConvertResultInfo().getTrueConvertResultInfo(data);
    }
    return new GetConvertResultInfo().getTrueConvertResultInfo(fileOutputStream);
  }

  public ConvertResultInfo convertPptToPptx(byte[] data) {
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    Presentation ppt = new GetPptDocumentObject().getPpt(data);
    ppt.save(fileOutputStream, SaveFormat.Pptx);
    return new GetConvertResultInfo().getTrueConvertResultInfo(fileOutputStream);
  }

  public ConvertResultInfo convertXlsToXlsx(byte[] data) {
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    Workbook workbook = new GetExcelDocumentObject().getWorkBook(data);
    try {
      workbook.save(fileOutputStream, com.aspose.cells.SaveFormat.XLSX);
    } catch (Exception e) {
      log.info(e.toString());
      return new GetConvertResultInfo().getTrueConvertResultInfo(data);
    } finally {
      workbook.dispose();
    }
    return new GetConvertResultInfo().getTrueConvertResultInfo(fileOutputStream);
  }

  public ConvertResultInfo convertWordAllPageToPng(byte[] data) {
    ConvertResultInfo convertResultInfo = new ConvertResultInfo();
    //获取用户当前临时文件夹路径
    String sysTempPath = System.getProperty("java.io.tmpdir") + File.separator;
    String office2pngTempPath = String.valueOf(Paths.get(sysTempPath, "dps", "office2png", String.valueOf(UUID.randomUUID())));
    String office2pngzipTempPath = String.valueOf(Paths.get(sysTempPath, "dps", "office2pngzip", String.valueOf(UUID.randomUUID())));
    Document doc = new GetWordsDocumentObject().getWord(data);
    try {
      int pageCount = doc.getPageCount();
      //将文档分别转换为单张图片，并存放在office2pngTempPath 指定的路径
      for (int i = 0; i < pageCount; i++) {
        String fileName = office2pngTempPath + "\\" + i + ".png";
        ImageSaveOptions imageOptions = new com.aspose.words.ImageSaveOptions(com.aspose.words.SaveFormat.PNG);
        imageOptions.setUseHighQualityRendering(true);
        imageOptions.setPageSet(new PageSet(i));
        doc.save(fileName, imageOptions);
      }
      File directory = new File(office2pngzipTempPath);
      if (directory.mkdirs()) {
        String zipFileName = office2pngzipTempPath + "\\" + UUID.randomUUID() + ".zip";
        InputStream in = new FileInputStream(ZipUtil.zip(office2pngTempPath, zipFileName));
        convertResultInfo.setSuccess(true);
        convertResultInfo.setBytes(toByteArray(in));
        in.close();
      }
    } catch (Exception e) {
      log.error(e.toString());
      return new GetConvertResultInfo().getFalseConvertResultInfo(e.toString());
    } finally {
      //递归删除临时生成的图片文件夹以及压缩包所在的文件夹
      deleteTempDirectory(new File(office2pngTempPath));
      deleteTempDirectory(new File(office2pngzipTempPath));
    }
    return convertResultInfo;
  }

  public ConvertResultInfo convertPptAllPageToPng(byte[] data) {
    ConvertResultInfo converResultInfo = new ConvertResultInfo();
    //获取用户当前临时文件夹路径
    String sysTempPath = System.getProperty("java.io.tmpdir") + File.separator;
    String office2pngTempPath = String.valueOf(Paths.get(sysTempPath, "dps", "office2png", String.valueOf(UUID.randomUUID())));
    String office2pngzipTempPath = String.valueOf(Paths.get(sysTempPath, "dps", "office2pngzip", String.valueOf(UUID.randomUUID())));
    String zipFileName = office2pngzipTempPath + "\\" + UUID.randomUUID() + ".zip";
    try {
      Presentation ppt = new GetPptDocumentObject().getPpt(data);
      if (new File(office2pngTempPath).mkdirs()) {
        for (ISlide slide : ppt.getSlides()) {
          Dimension size = new Dimension(1280, 720);
          //设置生成图片的大小
          BufferedImage bufferedImage = slide.getThumbnail(size);
          File outputFile = new File(office2pngTempPath + "\\" + slide.getSlideNumber() + ".png");
          ImageIO.write(bufferedImage, "PNG", outputFile);
        }
      }
      //释放资源
      ppt.dispose();
      File zipDirectory = new File(office2pngzipTempPath);
      if (zipDirectory.mkdirs()) {
        InputStream in = new FileInputStream(ZipUtil.zip(office2pngTempPath, zipFileName));
        converResultInfo.setSuccess(true);
        converResultInfo.setBytes(toByteArray(in));
        in.close();
      }
    } catch (IOException e) {
      converResultInfo.setSuccess(false);
      converResultInfo.setErrorMsg(e.toString());
    } finally {
      deleteTempDirectory(new File(office2pngTempPath));
      deleteTempDirectory(new File(office2pngzipTempPath));
    }
    return converResultInfo;
  }

  public ConvertResultInfo convertPdfAllPageToPng(byte[] data) {
    ConvertResultInfo converResultInfo = new ConvertResultInfo();
    //获取用户当前临时文件夹路径
    String sysTempPath = System.getProperty("java.io.tmpdir") + File.separator;
    String office2pngTempPath = String.valueOf(Paths.get(sysTempPath, "dps", "office2png", String.valueOf(UUID.randomUUID())));
    String office2pngzipTempPath = String.valueOf(Paths.get(sysTempPath, "dps", "office2pngzip", String.valueOf(UUID.randomUUID())));
    String zipFileName = office2pngzipTempPath + "\\" + UUID.randomUUID() + ".zip";
    // devices 包负责将PDF转为图像  Resolution对象负责转为图像的分辨率设置
    try {
      com.aspose.pdf.Document pdf = new GetPdfDocumentObject().getPdf(data);
      int pageCount = pdf.getPages().size();
      com.aspose.pdf.devices.Resolution imageResolution = new com.aspose.pdf.devices.Resolution(200);
      com.aspose.pdf.devices.PngDevice rendererPng = new com.aspose.pdf.devices.PngDevice(imageResolution);
      if (new File(office2pngTempPath).mkdirs()) {
        //pdf 下标从1开始
        for (int i = 1; i < pageCount; i++) {
          rendererPng.process(pdf.getPages().get_Item(i), new FileOutputStream(office2pngTempPath + "\\" + i + ".png"));
        }
      }
      //释放资源
      pdf.close();
      File zipDirectory = new File(office2pngzipTempPath);
      if (zipDirectory.mkdirs()) {
        InputStream in = new FileInputStream(ZipUtil.zip(office2pngTempPath, zipFileName));
        converResultInfo.setSuccess(true);
        converResultInfo.setBytes(toByteArray(in));
        in.close();
      }
    } catch (FileNotFoundException e) {
      converResultInfo.setSuccess(false);
      converResultInfo.setErrorMsg(e.toString());
      return converResultInfo;
    } catch (IOException e) {
      converResultInfo.setSuccess(false);
      converResultInfo.setErrorMsg(e.toString());
      return converResultInfo;
    } finally {
      deleteTempDirectory(new File(office2pngTempPath));
      deleteTempDirectory(new File(office2pngzipTempPath));
    }
    return converResultInfo;
  }

  public ConvertResultInfo convertWordOnePageToPng(byte[] data, int page) {
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    try {
      com.aspose.words.Document doc = new GetWordsDocumentObject().getWord(data);
      if (page >= doc.getPageCount() || page < 0) {
        return new GetConvertResultInfo().getFalseConvertResultInfo("页码参数错误");
      } else {
        ImageSaveOptions imageOptions = new com.aspose.words.ImageSaveOptions(com.aspose.words.SaveFormat.PNG);
        imageOptions.setUseHighQualityRendering(true);
        imageOptions.setPageSet(new PageSet(page));
        doc.save(fileOutputStream, imageOptions);
        return new GetConvertResultInfo().getTrueConvertResultInfo(fileOutputStream);
      }
    } catch (Exception e) {
      log.error(e.toString());
      return new GetConvertResultInfo().getFalseConvertResultInfo("页码参数错误");
    }
  }

  public ConvertResultInfo convertPptOnePageToPng(byte[] data, int page) {
    //下标从0开始，也就是说，传入3，返回的是第4页PPT
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    com.aspose.slides.Presentation ppt = new GetPptDocumentObject().getPpt(data);
    //设置要转换的图片的格式
    Dimension size = new Dimension(1280, 720);
    //获得指定页码的幻灯片
    ISlide slide = ppt.getSlides().get_Item(page);
    //转换为图片流格式
    BufferedImage bufferedImage = slide.getThumbnail(size);
    try {
      ImageIO.write(bufferedImage, "PNG", fileOutputStream);
    } catch (IOException e) {
      log.error(e.toString());
      return new GetConvertResultInfo().getFalseConvertResultInfo(e.toString());
    } finally {
      if (ppt != null) {
        ppt.dispose();
      }
    }
    return new GetConvertResultInfo().getTrueConvertResultInfo(fileOutputStream);
  }

  public ConvertResultInfo convertPdfOnePageToPng(byte[] data, int page) {
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();

    com.aspose.pdf.Document pdf = new GetPdfDocumentObject().getPdf(data);
    if (page >= pdf.getPages().size() || page < 0) {
      return new GetConvertResultInfo().getFalseConvertResultInfo("页码错误");
    }
    com.aspose.pdf.devices.Resolution imageResolution = new com.aspose.pdf.devices.Resolution(128);
    com.aspose.pdf.devices.PngDevice rendererPng = new com.aspose.pdf.devices.PngDevice(imageResolution);
    rendererPng.process(pdf.getPages().get_Item(page), fileOutputStream);
    //释放资源
    pdf.close();
    return new GetConvertResultInfo().getTrueConvertResultInfo(fileOutputStream);
  }

  public ConvertResultInfo convertWordOnePageToPdf(byte[] data, int page) {
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    try {
      Document doc = new GetWordsDocumentObject().getWord(data);
      if (page >= doc.getPageCount() || page < 0) {
        return new GetConvertResultInfo().getFalseConvertResultInfo("页码参数错误");
      } else {
        PdfSaveOptions pdfSaveOptions = new com.aspose.words.PdfSaveOptions();
        pdfSaveOptions.setPageSet(new PageSet(page));
        //0 嵌入所有字体 1 嵌入了除标准Windows字体Arial和Times New Roman之外的所有字体  2 不嵌入任何字体。
        pdfSaveOptions.setFontEmbeddingMode(0);
        //0 文档的显示方式留给 PDF 查看器。通常，查看器会显示适合页面宽度的文档。 1 使用指定的缩放系数显示页面。 2 显示页面，使其完全可见。 3 适合页面的宽度。 4 适合页面的高度。 5 适合边界框（包含页面上所有可见元素的矩形）。
        pdfSaveOptions.setZoomBehavior(0);
        doc.save(fileOutputStream, pdfSaveOptions);
        return new GetConvertResultInfo().getTrueConvertResultInfo(fileOutputStream);
      }
    } catch (Exception e) {
      log.error(e.toString());
      return new GetConvertResultInfo().getFalseConvertResultInfo(e.toString());
    }
  }

  public ConvertResultInfo convertPptOnePageToPdf(byte[] data, int page) {
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    com.aspose.slides.Presentation ppt = new GetPptDocumentObject().getPpt(data);
    try {
      int[] page2 = {page};
      ppt.save(fileOutputStream, page2,com.aspose.slides.SaveFormat.Pdf);
    } finally {
      if (ppt != null) {
        ppt.dispose();
      }
    }
    return new GetConvertResultInfo().getTrueConvertResultInfo(fileOutputStream);
  }
}
