package com.facishare.document.preview.cgi.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * Created by liuq on 16/9/7.
 */
public class DocPageCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(DocPageCalculator.class);

    public static int GetDocPageCount(byte[] data, String filePath) throws Exception {
        LOG.info("begin get pageCount,data length:{}",data.length);
        int pageCount = 0;
        DocType docType = DocTypeHelper.getDocType(filePath);
        switch (docType) {
            case Word:
                pageCount = parseWord(data);
                break;
            case Excel:
                pageCount = parseExcel(data);
                break;
            case PPT:
                pageCount = parsePPT(data);
                break;
            case PDF:
                pageCount = parsePDF(data);
        }
        LOG.info("end get pageCount");
        return pageCount;
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

    private static int parseWord(byte[] data) throws Exception {
        int version = checkFileVersion(data);
        return version == 2003 ? parseWord2003(data) : parseWord2007(data);
    }

    private static int parseExcel(byte[] data) throws Exception {
        int version = checkFileVersion(data);
        return version == 2003 ? parseExcel2003(data) : parseExcel2007(data);
    }

    private static int parsePPT(byte[] data) throws Exception {
        InputStream input = new ByteArrayInputStream(data);
        int version = checkFileVersion(data);
        return version == 2003 ? parsePPT2003(data) : parsePPT2007(data);
    }


    private static int parsePDF(byte[] data) throws IOException {
        InputStream input = new ByteArrayInputStream(data);
        PDDocument doc = PDDocument.load(input);
        return doc.getNumberOfPages();
    }

    private static int parseWord2007(byte[] data) throws Exception {
        InputStream input = new ByteArrayInputStream(data);
        XWPFDocument docx = new XWPFDocument(input);
        return docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();//总页数
    }

    private static int parseWord2003(byte[] data) throws Exception {
        InputStream input = new ByteArrayInputStream(data);
        WordExtractor doc = new WordExtractor(input);
        return doc.getSummaryInformation().getPageCount();//总页数
    }

    private static int parseExcel2007(byte[] data) throws Exception {
        InputStream input = new ByteArrayInputStream(data);
        XSSFWorkbook workbook = new XSSFWorkbook(input);
        return workbook.getNumberOfSheets();
    }

    private static int parseExcel2003(byte[] data) throws Exception {
        InputStream input = new ByteArrayInputStream(data);
        HSSFWorkbook hs = new HSSFWorkbook(input);
        return hs.getNumberOfSheets();
    }

    private static int parsePPT2007(byte[] data) throws Exception {
        InputStream input = new ByteArrayInputStream(data);
        XMLSlideShow ppt = new XMLSlideShow(input);
        return ppt.getSlides().size();
    }

    private static int parsePPT2003(byte[] data) throws Exception {
        InputStream input = new ByteArrayInputStream(data);
        SlideShow ppt = new HSLFSlideShow(input);
        return ppt.getSlides().size();
    }
}
