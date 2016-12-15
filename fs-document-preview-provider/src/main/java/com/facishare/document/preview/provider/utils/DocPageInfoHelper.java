package com.facishare.document.preview.provider.utils;

import application.dcs.IPICConvertor;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.common.utils.DocType;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import com.facishare.document.preview.provider.convertor.ConvertorPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
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
            log.error("parse excel happened error,path:{}!", filePath, ex);
        }
        return pageInfo;
    }

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
            log.error("parse excel happened error,path:{}!", filePath, e);
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            log.error("parse excel happened error,path:{}!", filePath, ex);
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
