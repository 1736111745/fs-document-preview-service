package com.facishare.document.preview.provider.utils;

import application.dcs.IPICConvertor;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.common.utils.DocType;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuq on 16/9/7.
 */
@Slf4j
public class DocPageInfoHelper {
    public static PageInfo GetPageInfo(byte[] data, String filePath) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("begin get page count,filePath:{}", filePath);
        try {
            DocType docType = DocTypeHelper.getDocType(filePath);
            switch (docType) {
                case Word:
                    return pareWord(filePath,data);
                case Excel:
                    return parseExcel(filePath,data);
                case PPT:
                    return parsePPT(filePath,data);
                case PDF:
                    return parsePDF(filePath,data);
                default:
                    return new PageInfo();
            }
        } catch (Exception e) {
            return new PageInfo();
        } finally {
            stopWatch.stop();
            log.info("get page count done,filePath:{},cost:{}", filePath, stopWatch.getTime() + "ms");
        }
    }


    private static int checkFileVersion(byte[] data) throws IOException {
        InputStream inp = new ByteArrayInputStream(data);
        if (!inp.markSupported()) {
            inp = new PushbackInputStream(inp, 8);
        }
        if (POIFSFileSystem.hasPOIFSHeader(inp)) {
            return 2003;
        }
        if (POIXMLDocument.hasOOXMLHeader(inp)) {
            return 2007;
        }
        return 2003;
    }

    private static PageInfo parsePPT(String filePath,byte[] data) throws IOException {
        int version = checkFileVersion(data);
        return version == 2003 ? parsePPT2003(filePath, data) : parsePPT2007(filePath, data);
    }

    private static PageInfo parsePPT2007(String filePath, byte[] data) {
        PageInfo pageInfo = new PageInfo();
        try {
            InputStream input = new ByteArrayInputStream(data);
            XMLSlideShow ppt = new XMLSlideShow(input);
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(ppt.getSlides().size());
        } catch (EncryptedDocumentException e) {
            pageInfo.setSuccess(false);
            pageInfo.setErrorMsg("该文档是为加密文档，暂不支持预览！");
            log.error("parse excel happened error,path:{}!", filePath, e);
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            log.error("parse excel happened error,path:{}!", filePath, ex);
        } finally {
            return pageInfo;
        }
    }

    private static PageInfo parseWord2007(String filePath, byte[] data) throws Exception {
        PageInfo pageInfo = new PageInfo();
        try {
            InputStream input = new ByteArrayInputStream(data);
            XWPFDocument docx = new XWPFDocument(input);
            int pageCount = docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();//总页数
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(pageCount);
        } catch (EncryptedDocumentException e) {
            pageInfo.setSuccess(false);
            pageInfo.setErrorMsg("该文档是为加密文档，暂不支持预览！");
            log.error("parse excel happened error,path:{}!", filePath, e);
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            log.error("parse excel happened error,path:{}!", filePath, ex);
        } finally {
            return pageInfo;
        }

    }

    private static PageInfo
    parseWord2003(String filePath, byte[] data) throws Exception {
        PageInfo pageInfo = new PageInfo();
        try {
            InputStream input = new ByteArrayInputStream(data);
            WordExtractor doc = new WordExtractor(input);
            int pageCount = doc.getSummaryInformation().getPageCount();//总页数
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(pageCount);
        } catch (EncryptedDocumentException e) {
            pageInfo.setSuccess(false);
            pageInfo.setErrorMsg("该文档是为加密文档，暂不支持预览！");
            log.error("parse excel happened error,path:{}!", filePath, e);
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            log.error("parse excel happened error,path:{}!", filePath, ex);
        } finally {
            return pageInfo;
        }
    }

    private static PageInfo parsePPT2003(String filePath, byte[] data) {
        PageInfo pageInfo = new PageInfo();
        try {
            InputStream input = new ByteArrayInputStream(data);
            SlideShow ppt = new HSLFSlideShow(input);
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(ppt.getSlides().size());
        } catch (EncryptedDocumentException e) {
            pageInfo.setSuccess(false);
            pageInfo.setErrorMsg("该文档是为加密文档，暂不支持预览！");
            log.error("parse excel happened error,path:{}!", filePath, e);
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            log.error("parse excel happened error,path:{}!", filePath, ex);
        } finally {
            return pageInfo;
        }
    }

    private static PageInfo parseExcel(String filePath,byte[] data) throws Exception {
        int version = checkFileVersion(data);
        return version == 2003 ? parseExcel2003(filePath, data) : parseExcel2007(filePath, data);
    }


    private static PageInfo parsePDF(String filePath,byte[] data) throws IOException {
        PageInfo pageInfo = new PageInfo();
        try {
            InputStream input = new ByteArrayInputStream(data);
            PDDocument doc = PDDocument.load(input);
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(doc.getNumberOfPages());
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            log.error("parse excel happened error,path:{}!", filePath, ex);
        }
        return pageInfo;
    }

    private static PageInfo pareWord(String filePath,byte[] data) throws Exception {
        int version = checkFileVersion(data);
        return version == 2003 ? parseWord2003(filePath, data) : parseWord2007(filePath, data);
    }

    private static PageInfo parseExcel2007(String filePath, byte[] data) throws Exception {
        PageInfo pageInfo = new PageInfo();
        try {
            InputStream input = new ByteArrayInputStream(data);
            XSSFWorkbook workbook = new XSSFWorkbook(input);
            int pageCount = workbook.getNumberOfSheets();
            List<String> sheetNames = new ArrayList<>();
            for (int i = 0; i < pageCount; i++) {
                XSSFSheet xssfSheet = workbook.getSheetAt(i);
                String sheetName = xssfSheet.getSheetName();
                boolean isHidden = workbook.isSheetHidden(i);
                String hiddenFlag = isHidden ? "_$h1$" : "";
                boolean isActive = workbook.getActiveSheetIndex() == i;
                String activeFlag = isActive ? "_$a1$" : "";
                sheetName = sheetName + hiddenFlag + activeFlag;
                sheetNames.add(sheetName);
            }
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(pageCount);
            pageInfo.setSheetNames(sheetNames);
            return pageInfo;
        } catch (EncryptedDocumentException e) {
            pageInfo.setSuccess(false);
            pageInfo.setErrorMsg("该文档是为加密文档，暂不支持预览！");
            log.error("parse excel happened error,path:{}!", filePath, e);
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            log.error("parse excel happened error,path:{}!", filePath, ex);
        }
        return pageInfo;
    }

    private static PageInfo parseExcel2003(String filePath, byte[] data) throws Exception {
        PageInfo pageInfo = new PageInfo();
        try {
            InputStream input = new ByteArrayInputStream(data);
            HSSFWorkbook hs = new HSSFWorkbook(input);
            int pageCount = hs.getNumberOfSheets();
            List<String> sheetNames = new ArrayList<>();
            for (int i = 0; i < pageCount; i++) {
                HSSFSheet xssfSheet = hs.getSheetAt(i);
                String sheetName = xssfSheet.getSheetName();
                boolean isHidden = hs.isSheetHidden(i);
                String hiddenFlag = isHidden ? "_$h1$" : "_$h0$";
                boolean isActive = xssfSheet.isActive();
                String activeFlag = isActive ? "_$a1$" : "_$a0$";
                sheetName = sheetName + hiddenFlag + activeFlag;
                sheetNames.add(sheetName);
            }
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(pageCount);
            pageInfo.setSheetNames(sheetNames);
        } catch (EncryptedDocumentException e) {
            pageInfo.setSuccess(false);
            pageInfo.setErrorMsg("该文档是为加密文档，暂不支持预览！");
            log.error("parse excel happened error,path:{}!", filePath, e);
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            log.error("parse excel happened error,path:{}!", filePath, ex);
        }
        return pageInfo;
    }
}
