package com.facishare.document.preview.cgi.utils;

import application.dcs.IPICConvertor;
import com.dyuproject.protostuff.StringSerializer;
import com.facishare.document.preview.cgi.convertor.ConvertorPool;
import com.facishare.document.preview.cgi.model.DocPageInfo;
import com.facishare.document.preview.cgi.model.PageInfo;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuq on 16/9/7.
 */
public class DocPageInfoHelper {
    private static final Logger LOG = LoggerFactory.getLogger(DocPageInfoHelper.class);

    public static PageInfo GetPageInfo(byte[] data, String filePath) throws Exception {
        DocType docType = DocTypeHelper.getDocType(filePath);
        switch (docType) {
            case Word:
                return parseWordAndPPT(filePath);
            case Excel:
                return parseExcel(data, filePath);
            case PPT:
                return parseWordAndPPT(filePath);
            case PDF:
                return parsePDF(data, filePath);
            default:
                return new PageInfo();
        }
    }

    public static DocPageInfo GetDocPageInfo(String filePath) throws Exception {
        String docFormat = "", previewFormat = "", contentType = "";
        DocType docType = DocTypeHelper.getDocType(filePath);
        switch (docType) {
            case Word: {
                docFormat = "101";
                previewFormat = "1";
                contentType = "image/png";
                break;
            }
            case Excel: {
                docFormat = "102";
                previewFormat = "2";
                contentType = "text/html";
                break;
            }
            case PPT: {
                docFormat = "103";
                previewFormat = "1";
                contentType = "image/png";
                break;
            }
            case PDF: {
                docFormat = "104";
                previewFormat = "1";
                contentType = "image/png";
            }
        }
        DocPageInfo docPageInfo = new DocPageInfo();
        docPageInfo.setContentType(contentType);
        docPageInfo.setDocumentFormat(docFormat);
        docPageInfo.setPreviewFormat(previewFormat);
        return docPageInfo;
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


    private static PageInfo parseExcel(byte[] data, String filePath) throws Exception {
        int version = checkFileVersion(data);
        return version == 2003 ? parseExcel2003(filePath, data) : parseExcel2007(filePath, data);
    }


    private static PageInfo parsePDF(byte[] data, String filePath) throws IOException {
        PageInfo pageInfo = new PageInfo();
        try {
            InputStream input = new ByteArrayInputStream(data);
            PDDocument doc = PDDocument.load(input);
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(doc.getNumberOfPages());
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            LOG.error("parse excel happened error,path:{}!", filePath, ex);
        }
        return pageInfo;
    }


//    private static int parseWord2007(byte[] data) throws Exception {
//        InputStream input = new ByteArrayInputStream(data);
//        XWPFDocument docx = new XWPFDocument(input);
//        return docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();//总页数
//    }
//
//    private static int parseWord2003(byte[] data,String filePath) throws Exception {
//        InputStream input = new ByteArrayInputStream(data);
//        WordExtractor doc = new WordExtractor(input);
//        return doc.getSummaryInformation().getPageCount();//总页数
//    }

    private static PageInfo parseWordAndPPT(String filePath) {
        PageInfo pageInfo = new PageInfo();
        ConvertorPool.ConvertorObject convertObj = ConvertorPool.getInstance().getConvertor();
        try {
            IPICConvertor ipicConvertor = convertObj.convertor.convertMStoPic(filePath);
            int pageCount = ipicConvertor.getPageCount();
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(pageCount);
            ipicConvertor.close();
        } catch (EncryptedDocumentException e) {
            pageInfo.setSuccess(false);
            pageInfo.setErrorMsg("该文档是为加密文档，暂不支持预览！");
            LOG.error("parse excel happened error,path:{}!", filePath, e);
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            LOG.error("parse excel happened error,path:{}!", filePath, ex);
        } finally {
            ConvertorPool.getInstance().returnConvertor(convertObj);
        }
        return pageInfo;
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
            LOG.error("parse excel happened error,path:{}!", filePath, e);
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            LOG.error("parse excel happened error,path:{}!", filePath, ex);
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
            LOG.error("parse excel happened error,path:{}!", filePath, e);
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            LOG.error("parse excel happened error,path:{}!", filePath, ex);
        }
        return pageInfo;
    }

//    private static int parsePPT2007(byte[] data) throws Exception {
//        InputStream input = new ByteArrayInputStream(data);
//        XMLSlideShow ppt = new XMLSlideShow(input);
//        return ppt.getSlides().size();
//    }
//
//    private static int parsePPT2003(byte[] data) throws Exception {
//        InputStream input = new ByteArrayInputStream(data);
//        SlideShow ppt = new HSLFSlideShow(input);
//        return ppt.getSlides().size();
//    }
}
