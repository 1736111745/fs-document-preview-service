package com.facishare.document.preview.cgi.utils;

import org.apache.commons.io.FilenameUtils;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by liuq on 16/9/7.
 */
public class DocPageCalculator {
    public static int GetDocPageCount(String filePath) throws Exception {
        int pageCount=0;
        String extension= FilenameUtils.getExtension(filePath).toLowerCase();
        switch (extension) {
            case "doc":
                pageCount = parseWord97(filePath);
                break;
            case "docx":
                pageCount = parseWord2007(filePath);
                break;
            case "xls":
                pageCount = parseExcel97(filePath);
                break;
            case "xlsx":
                pageCount = parseExcel2007(filePath);
                break;
            case "ppt":
                pageCount = parsePPT97(filePath);
                break;
            case "pptx":
                pageCount = parsePPT2007(filePath);
                break;
            case "pdf":
                pageCount = parsePDF(filePath);
                break;
        }
        return pageCount;
    }

    private static int parseWord2007(String filePath) throws Exception {
        XWPFDocument docx = new XWPFDocument(POIXMLDocument.openPackage(filePath));
        return docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();//总页数
    }

    private static int parseWord97(String filePath) throws Exception {
        WordExtractor doc = new WordExtractor(new FileInputStream(filePath));
        return doc.getSummaryInformation().getPageCount();//总页数
    }

    private static int parseExcel2007(String filePath) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook(POIXMLDocument.openPackage(filePath));
        return workbook.getNumberOfSheets();
    }

    private static int parseExcel97(String filePath) throws Exception {
        HSSFWorkbook hs = new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(filePath)));
        return hs.getNumberOfSheets();
    }

    private static int parsePPT2007(String filePath) throws Exception {
        XMLSlideShow ppt = new XMLSlideShow(POIXMLDocument.openPackage(filePath));
        return ppt.getSlides().size();
    }

    private static int parsePPT97(String filePath) throws Exception {
        SlideShow ppt = new HSLFSlideShow(new FileInputStream(filePath));
        return ppt.getSlides().size();
    }

    private static  int parsePDF(String filePath) throws IOException {
        PDDocument doc = PDDocument.load(new File(filePath));
        return doc.getNumberOfPages();
    }

//    public static void main(String[] args) throws Exception {
//        String file="/Users/liuq/Downloads/批量下载/numbers生成的Excel文档.xlsx";
//        int pageCount=GetDocPageCount(file);
//    }
}
