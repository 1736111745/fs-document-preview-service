package com.facishare.document.preview.common.utils.aspose;

import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;
import com.facishare.document.preview.common.model.DocType;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuq on 2017/4/17.
 */
@Slf4j
@Component
public class PageInfoHelper {

    private final static String errorMsg = "该文档是为加密文档或者已经损坏，暂不支持预览!";

    public PageInfoHelper() {
        LicenceHelper.setAllLicence();
    }
    public  PageInfo getPageInfo(String npath, String filePath) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("begin get page count,filePath:{}", filePath);
        try {
            DocType docType = DocTypeHelper.getDocType(filePath);
            switch (docType) {
                case Word:
                    return parseWord(npath, filePath);
                case Excel:
                    return parseExcel(npath, filePath);
                case PPT:
                    return parsePpt(npath, filePath);
                case PDF:
                    return parsePdf(npath, filePath);
                default:
                    return new PageInfo();
            }
        } catch (Exception e) {
            log.error("get page info error!", e);
            return new PageInfo();
        } finally {
            stopWatch.stop();
            log.info("get page count done,filePath:{},cost:{}", filePath, stopWatch.getTime() + "ms");
        }
    }


    private  PageInfo parseWord(String npath, String filePath) {
        PageInfo pageInfo = new PageInfo();
        try {
            boolean isEncrypted = com.aspose.words.FileFormatUtil.detectFileFormat(filePath).isEncrypted();
            if (!isEncrypted) {
                Document document = new Document(filePath);
                pageInfo.setSuccess(true);
                pageInfo.setPageCount(document.getPageCount());
            } else {
                log.warn("get page count error!path:{}", npath);
                pageInfo.setSuccess(false);
                pageInfo.setPageCount(0);
                pageInfo.setErrorMsg(errorMsg);
            }
        } catch (Exception e) {
            log.warn("get page count error!path:{}", npath, e);
            pageInfo.setSuccess(false);
            pageInfo.setPageCount(0);
            pageInfo.setErrorMsg(errorMsg);
        }
        return pageInfo;
    }

    private  PageInfo parseExcel(String npath, String filePath) throws Exception {
        PageInfo pageInfo = new PageInfo();
        try {
            boolean isEncrypted = com.aspose.cells.FileFormatUtil.detectFileFormat(filePath).isEncrypted();
            if (!isEncrypted) {
                Workbook workbook = new Workbook(filePath);
                List<String> sheetNames = new ArrayList<>();
                WorksheetCollection sheets = workbook.getWorksheets();
                for (int i = 0; i < sheets.getCount(); i++) {
                    Worksheet worksheet = sheets.get(i);
                    String sheetName = worksheet.getName();
                    boolean isHidden = !worksheet.isVisible();
                    String hiddenFlag = isHidden ? "_$h1$" : "_$h0$";
                    boolean isActive = sheets.getActiveSheetIndex() == i;
                    String activeFlag = isActive ? "_$a1$" : "_$a0$";
                    sheetName = sheetName + hiddenFlag + activeFlag;
                    sheetNames.add(sheetName);
                }
                pageInfo.setSuccess(true);
                pageInfo.setPageCount(sheets.getCount());
                pageInfo.setSheetNames(sheetNames);
            } else {
                log.warn("get page count error!path:{}", npath);
                pageInfo.setSuccess(false);
                pageInfo.setPageCount(0);
                pageInfo.setErrorMsg(errorMsg);
            }
        } catch (Exception e) {
            log.warn("get page count error!path:{}", npath, e);
            pageInfo.setSuccess(false);
            pageInfo.setPageCount(0);
            pageInfo.setErrorMsg(errorMsg);
        }
        return pageInfo;
    }

    private  PageInfo parsePpt(String npath, String filePath) {
        PageInfo pageInfo = new PageInfo();
        try {
            boolean isEncrypted = com.aspose.words.FileFormatUtil.detectFileFormat(filePath).isEncrypted();
            if (!isEncrypted) {
                Presentation presentation = new Presentation(filePath);
                pageInfo.setSuccess(true);
                pageInfo.setPageCount(presentation.getSlides().size());
            } else {
                log.warn("get page count error!path:{}", npath);
                pageInfo.setSuccess(false);
                pageInfo.setPageCount(0);
                pageInfo.setErrorMsg(errorMsg);
            }
        } catch (Exception e) {
            log.warn("get page count error!path:{}", npath, e);
            pageInfo.setSuccess(false);
            pageInfo.setPageCount(0);
            pageInfo.setErrorMsg(errorMsg);
        }
        return pageInfo;
    }

    private  PageInfo parsePdf(String npath, String filePath) {
        LicenceHelper.setPptLicence();
        PageInfo pageInfo = new PageInfo();
        try {
            com.aspose.pdf.Document document = new com.aspose.pdf.Document(filePath);
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(document.getPages().size());
        } catch (Exception e) {
            log.warn("get page count error!path:{}", npath);
            pageInfo.setSuccess(false);
            pageInfo.setPageCount(0);
            pageInfo.setErrorMsg(errorMsg);
        }
        return pageInfo;
    }

    public static void main(String[] args) {

    }
}

