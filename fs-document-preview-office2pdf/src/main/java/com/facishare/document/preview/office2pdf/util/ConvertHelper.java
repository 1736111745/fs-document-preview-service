package com.facishare.document.preview.office2pdf.util;

import cn.hutool.core.util.ZipUtil;
import com.aspose.cells.HtmlSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.aspose.slides.ISlide;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.PageSet;
import com.aspose.words.SaveFormat;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.office2pdf.model.ConverResultInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [获得 Excel、ppt、pdf、word的页码信息（是否加锁、是否可见、页码总数等]
 * @createTime : [2022/3/31 15:11]
 * @updateUser : [Andy]
 * @updateTime : [2022/3/31 15:11]
 * @updateRemark : [新建本类]
 */
public class ConvertHelper {

  public static PageInfo GetDocPageInfo(byte[] data, String filePath) throws Exception {

    //打印日志
    PageInfo pageInfo = new PageInfo();
    /*
     * 获取文件后缀名字
     * 根据文件路径，获得最后一个 . 分割符的索引位置从索引位置截取一个子字符串 并转换为小写。
     * */
    String fileSuffix = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
    if (fileSuffix != null) {
      switch (fileSuffix) {
        case ".doc":
        case ".docx":
          pageInfo = GetWordPageInfo(data, filePath);
          break;
        case ".xls":
        case ".et":
          pageInfo = GetExcellPageInfo(data, filePath);
          break;
        case ".ppt":
        case ".pptx":
          pageInfo = GetPptPageInfo(data, filePath);
          break;
        case ".pdf":
          pageInfo = GetPdfPageInfo(data, filePath);
          break;
        default:
          pageInfo.setPageCount(0);
          pageInfo.setSuccess(false);
          pageInfo.setErrorMsg("不支持的文件类型");
      }
    } else {
      throw new Exception("file extension can not be null");
    }
    System.out.println("打印信息：" + pageInfo.getPageCount());
    return pageInfo;
  }

  private static PageInfo GetWordPageInfo(byte[] data, String filePath) throws IOException {
    PageInfo pageInfo = new PageInfo();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    try {
      if (!FileEncryptChecker.checkIsEncrypt(data, filePath)) {
        // 根据传来的文件，创建文档对象
        com.aspose.words.Document doc = new Document(fileInputStream);
        // 通过文件对象获得页码
        int pageCount = doc.getPageCount();
        pageInfo.setSuccess(true);
        pageInfo.setPageCount(pageCount);
      } else {
        //    FSTraceFinder.TraceMsgAsInfo("get pageConut error!path:{0}");
        pageInfo.setSuccess(false);
        pageInfo.setPageCount(0);
        pageInfo.setErrorMsg("FILE_ENCRYPT_ERROR_MSG");
      }
    } catch (Exception e) {
      //    FSTraceFinder.TraceMsgAsInfo("get pageConut error!path:{0}");
      pageInfo.setSuccess(false);
      pageInfo.setPageCount(0);
      pageInfo.setErrorMsg("FILE_DAMAGE_ERROR_MSG");
    } finally {
      try {
        fileInputStream.close();
      } catch (IOException e) {
        pageInfo.setSuccess(false);
        pageInfo.setPageCount(0);
        pageInfo.setErrorMsg("流关闭异常");
      }
    }
    return pageInfo;
  }

  private static PageInfo GetPptPageInfo(byte[] data, String filePath) throws IOException {
    PageInfo pageInfo = new PageInfo();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    try {
      if (!FileEncryptChecker.checkIsEncrypt(data, filePath)) {
        // 获得PPT总页码
        int pageCount = PowerPointHelper.GetPptxPageCount(data);
        if (pageCount == 0) {
          //    FSTraceFinder.TraceMsgAsInfo("get pageConut error!path:{0}");
          pageInfo.setSuccess(false);
          pageInfo.setPageCount(0);
          pageInfo.setErrorMsg("FILE_DAMAGE_ERROR_MSG");
        } else {
          pageInfo.setSuccess(true);
          pageInfo.setPageCount(pageCount);
        }
      } else {
        //    FSTraceFinder.TraceMsgAsInfo("get pageConut error!path:{0}");
        pageInfo.setSuccess(false);
        pageInfo.setPageCount(0);
        pageInfo.setErrorMsg("FILE_DAMAGE_ERROR_MSG");
      }
    } catch (Exception e) {
      //    FSTraceFinder.TraceMsgAsInfo("get pageConut error!path:{0}");
      pageInfo.setSuccess(false);
      pageInfo.setPageCount(0);
      pageInfo.setErrorMsg("FILE_DAMAGE_ERROR_MSG");
    } finally {
      try {
        fileInputStream.close();
      } catch (IOException e) {
        pageInfo.setSuccess(false);
        pageInfo.setPageCount(0);
        pageInfo.setErrorMsg("流关闭异常");
      }
    }
    return pageInfo;
  }

  private static PageInfo GetPdfPageInfo(byte[] data, String filePath) {

    PageInfo pageInfo = new PageInfo();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    try {
      if (!FileEncryptChecker.checkIsEncrypt(data, filePath)) {
        int pageCount = 0;
        com.aspose.pdf.Document pdf = new com.aspose.pdf.Document(data);
        pageCount = pdf.getPages().size();
        pageInfo.setSuccess(true);
        pageInfo.setPageCount(pageCount);
        pdf.close();
      } else {
        //    FSTraceFinder.TraceMsgAsInfo("get pageConut error!path:{0}");
        pageInfo.setSuccess(false);
        pageInfo.setPageCount(0);
        pageInfo.setErrorMsg("FILE_ENCRYPT_ERROR_MSG");
      }
    } catch (Exception e) {
      //    FSTraceFinder.TraceMsgAsInfo("get pageConut error!path:{0}");
      pageInfo.setSuccess(false);
      pageInfo.setPageCount(0);
      pageInfo.setErrorMsg("FILE_ENCRYPT_ERROR_MSG");
    } finally {
      try {
        fileInputStream.close();
      } catch (IOException e) {
        pageInfo.setSuccess(false);
        pageInfo.setPageCount(0);
        pageInfo.setErrorMsg("流关闭异常");
      }
    }
    return pageInfo;
  }

  private static PageInfo GetExcellPageInfo(byte[] data, String filePath) {
    PageInfo pageInfo = new PageInfo();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    try {
      if (!FileEncryptChecker.checkIsEncrypt(data, filePath)) {
        //Workbook对象代表 Excel整个工作簿
        Workbook workbook = new com.aspose.cells.Workbook(fileInputStream);
        List<String> sheetNames = new ArrayList<String>();
        // WorksheetCollection对象代表 Excel工作表的集合
        WorksheetCollection worksheetCollection = workbook.getWorksheets();
        for (int i = 0; i < worksheetCollection.getCount(); i++) {
          // Worksheet对象代表其中一个工作表
          Worksheet worksheet = worksheetCollection.get(i);
          //获得当前工作表的名称
          String sheetName = worksheet.getName();
          //判断当前工作表是否可见 可见返回true 这里boolean值取反
          boolean isHidden = !worksheet.isVisible();
          // 工作表可见 取_$h0$ 工作表不可见取_$h1$
          String hiddenFlag = isHidden ? "_$h1$" : "_$h0$";
          //判断当前活动的表的索引是否与遍历到的索引相一致 一致返回true
          boolean isActive = worksheetCollection.getActiveSheetIndex() == i;
          // 如果当前遍历的表就是活动的表，取_$a1$ 否则取_$a0$
          String activeFlag = isActive ? "_$a1$" : "_$a0$";
          //
          sheetName = sheetName + hiddenFlag + activeFlag;
          sheetNames.add(sheetName);
        }
        pageInfo.setSuccess(true);
        //返回当前工作簿中一共有多少张表
        pageInfo.setPageCount(worksheetCollection.getCount());
        //将 之前遍历循环获得的 每张表的信息（每表的表明，以及是否可见？是否是当前活动的表？）放入pageInfo并携带回去
        pageInfo.setSheetNames(sheetNames);
        //释放 Excel工作簿对象 workbook占据的资源
        workbook.dispose();
      }
    } catch (Exception e) {
      //    FSTraceFinder.TraceMsgAsInfo("get pageConut error!path:{0}");
      pageInfo.setSuccess(false);
      pageInfo.setPageCount(0);
      pageInfo.setErrorMsg("FILE_DAMAGE_ERROR_MSG");
    } finally {
      try {
        fileInputStream.close();
      } catch (IOException e) {
        pageInfo.setSuccess(false);
        pageInfo.setPageCount(0);
        pageInfo.setErrorMsg("流关闭异常");
      }
    }
    return pageInfo;
  }

  public static ConverResultInfo Excel2Html(byte[] data, int page) {
    ConverResultInfo converResultInfo = new ConverResultInfo();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    try {
      Workbook workbook = new Workbook(fileInputStream);
      WorksheetCollection worksheetCollection = workbook.getWorksheets();
      Worksheet worksheet = worksheetCollection.get(page);
      int pageCount = worksheetCollection.getCount();

      if (page >= pageCount || page < 0) {
        converResultInfo.setErrorMsg("页码参数错误");
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
          converResultInfo.setSuccess(true);
          converResultInfo.setBytes(fileOutputStream.toByteArray());
        }
      }
    } catch (Exception e) {
      converResultInfo.setErrorMsg(e.toString());
    } finally {
      try {
        fileInputStream.close();
        fileOutputStream.close();
      } catch (IOException e) {
        converResultInfo.setErrorMsg(e.toString());
      }
    }
    return converResultInfo;
  }


  public static ConverResultInfo Word2Pdf(byte[] data) {
    ConverResultInfo converResultInfo = new ConverResultInfo();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    Document doc = null;
    try {
      doc = new com.aspose.words.Document(fileInputStream);
      doc.save(fileOutputStream, com.aspose.words.SaveFormat.PDF);
      converResultInfo.setErrorMsg("");
      converResultInfo.setBytes(fileOutputStream.toByteArray());
      converResultInfo.setSuccess(true);
    } catch (Exception e) {
      converResultInfo.setErrorMsg(e.toString());
    } finally {
      try {
        fileInputStream.close();
        fileOutputStream.close();
      } catch (IOException e) {
        converResultInfo.setErrorMsg(e.toString());
      }
    }
    return converResultInfo;
  }

  public static ConverResultInfo Ppt2Pdf(byte[] data) {
    ConverResultInfo converResultInfo = new ConverResultInfo();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    try {
      Presentation ppt = new com.aspose.slides.Presentation(fileInputStream);
      ppt.save(fileOutputStream, com.aspose.slides.SaveFormat.Pdf);
      converResultInfo.setBytes(fileOutputStream.toByteArray());
      converResultInfo.setSuccess(true);
      converResultInfo.setErrorMsg("");
    } catch (Exception e) {
      converResultInfo.setSuccess(false);
      converResultInfo.setErrorMsg("ppt转PDF处理异常");
    } finally {
      try {
        fileInputStream.close();
        fileOutputStream.close();
      } catch (IOException e) {
        converResultInfo.setErrorMsg(e.toString());
      }
    }
    return converResultInfo;
  }

  public static ConverResultInfo Word2Png(byte[] data) {
    ByteArrayInputStream fileInputStream=new ByteArrayInputStream(data);
    ConverResultInfo converResultInfo = new ConverResultInfo();
    //获取用户当前临时文件夹路径
    String sysTempPath=System.getProperty("java.io.tmpdir")+ File.separator;
    String office2pngTempPath= String.valueOf(Paths.get(sysTempPath,"dps","office2png", String.valueOf(UUID.randomUUID())));
    String office2pngzipTempPath= String.valueOf(Paths.get(sysTempPath,"dps","office2pngzip", String.valueOf(UUID.randomUUID())));

    try {
      Document doc = new com.aspose.words.Document(fileInputStream);
      int pageCount = doc.getPageCount();
      //将文档分别转换为单张图片，并存放在office2pngTempPath 指定的路径
      for (int i = 0; i < pageCount; i++) {
        String fileName = office2pngTempPath + "\\" + i + ".png";
        ImageSaveOptions imageOptions = new com.aspose.words.ImageSaveOptions(SaveFormat.PNG);
        imageOptions.setUseHighQualityRendering(true);
        imageOptions.setPageSet(new PageSet(i));
        doc.save(fileName, imageOptions);
      }
      File directory = new File(office2pngzipTempPath);
      if (directory.mkdirs()) {
        String zipFileName = office2pngzipTempPath + "\\" + UUID.randomUUID() + ".zip";
        InputStream in = new FileInputStream(ZipUtil.zip(office2pngTempPath, zipFileName));
        converResultInfo.setSuccess(true);
        converResultInfo.setBytes(toByteArray(in));
        in.close();
      }
    }catch (Exception e){
      converResultInfo.setErrorMsg(e.toString());
      converResultInfo.setSuccess(false);
    }finally {
      //递归删除临时生成的图片文件夹以及压缩包所在的文件夹
      deleteTempDirectory(new File(office2pngTempPath));
      deleteTempDirectory(new File(office2pngzipTempPath));
    }
    return  converResultInfo;
  }

  private static byte[] toByteArray(InputStream in){
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024 * 4];
    int n = 0;
    try {
      while ((n = in.read(buffer)) != -1) {
        out.write(buffer, 0, n);
      }
    }catch (IOException e){

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

  public static ConverResultInfo Word2Png(byte[] data, int page) {
    ConverResultInfo converResultInfo = new ConverResultInfo();
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
    try {
      com.aspose.words.Document doc = new com.aspose.words.Document(fileInputStream);
      if (page >= doc.getPageCount() || page < 0) {
        converResultInfo.setSuccess(false);
        converResultInfo.setErrorMsg("页码参数错误");
        return converResultInfo;
      } else {
        ImageSaveOptions imageOptions = new com.aspose.words.ImageSaveOptions(SaveFormat.PNG);
        imageOptions.setUseHighQualityRendering(true);
        imageOptions.setPageSet(new PageSet(page));
        doc.save(fileOutputStream, imageOptions);
        converResultInfo.setSuccess(true);
        converResultInfo.setBytes(fileOutputStream.toByteArray());
      }
    } catch (Exception e) {
      converResultInfo.setErrorMsg("Word2Png方法，初始化Document对象失败");
    } finally {
      try {
        fileInputStream.close();
        fileOutputStream.close();
      } catch (IOException e) {
        converResultInfo.setErrorMsg("流关闭异常");
      }
    }
    return converResultInfo;
  }

  public static ConverResultInfo Ppt2Png(byte[] data) {
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);

    ConverResultInfo converResultInfo = new ConverResultInfo();
    //获取用户当前临时文件夹路径
    String sysTempPath = System.getProperty("java.io.tmpdir") + File.separator;
    String office2pngTempPath = String.valueOf(Paths.get(sysTempPath, "dps", "office2png", String.valueOf(UUID.randomUUID())));
    String office2pngzipTempPath = String.valueOf(Paths.get(sysTempPath, "dps", "office2pngzip", String.valueOf(UUID.randomUUID())));
    String zipFileName = office2pngzipTempPath + "\\" + UUID.randomUUID() + ".zip";
    try {
      com.aspose.slides.Presentation ppt = new com.aspose.slides.Presentation(fileInputStream);
      int pageCount = ppt.getSlides().size();
      if (new File(office2pngTempPath).mkdirs()){
        for (ISlide slide : ppt.getSlides()) {
          Dimension size=new Dimension(1280,720);
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
  public static ConverResultInfo Ppt2Png(byte[] data,int page) {
    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(data);
    ByteArrayOutputStream fileOutputStream=new ByteArrayOutputStream();
    ConverResultInfo converResultInfo = new ConverResultInfo();
    com.aspose.slides.Presentation ppt = new com.aspose.slides.Presentation(fileInputStream);
    //设置要转换的图片的格式
    Dimension size=new Dimension(1280,720);
    //获得指定页码的幻灯片
    ISlide slide=ppt.getSlides().get_Item(page);
    //转换为图片流格式
    BufferedImage bufferedImage=slide.getThumbnail(size);
    try {
      ImageIO.write(bufferedImage,"PNG",fileOutputStream);
    } catch (IOException e) {
      converResultInfo.setSuccess(false);
      converResultInfo.setErrorMsg(e.toString());
      return converResultInfo;
    }
    converResultInfo.setSuccess(true);
    converResultInfo.setBytes(fileOutputStream.toByteArray());
    return converResultInfo;
  }

  public static ConverResultInfo Pdf2Png(byte[] bytes, int page) {
    return null;
  }
}





































