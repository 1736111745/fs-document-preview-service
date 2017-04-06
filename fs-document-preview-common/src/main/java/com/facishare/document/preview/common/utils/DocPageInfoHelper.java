package com.facishare.document.preview.common.utils;

import com.facishare.document.preview.common.model.DocType;
import com.facishare.document.preview.common.model.PageInfo;
import com.fxiaoke.excel.Excel;
import com.fxiaoke.excel.Sheet;
import com.fxiaoke.pdf.PdfHelper;
import com.fxiaoke.ppt.PPTHelper;
import com.fxiaoke.word.WordHelper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuq on 2017/1/10.
 */
@Slf4j
@UtilityClass
public class DocPageInfoHelper {
    public PageInfo getPageInfo(String filePath) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("begin get page count,filePath:{}", filePath);
        try {
            DocType docType = DocTypeHelper.getDocType(filePath);
            switch (docType) {
                case Word:
                    return parseWord(filePath);
                case Excel:
                    return parseExcel(filePath);
                case PPT:
                    return parsePPT(filePath);
                case PDF:
                    return parsePdf(filePath);
                default:
                    return new PageInfo();
            }
        } finally {
            stopWatch.stop();
            log.info("get page count done,filePath:{},cost:{}", filePath, stopWatch.getTime() + "ms");
        }
    }


    private PageInfo parseExcel(String filePath) {
        PageInfo pageInfo = new PageInfo();
        Excel excel = new Excel(new File(filePath));
        List<Sheet> sheets = excel.parse();
        if (sheets.size() == 0) {
            pageInfo.setSuccess(false);
            pageInfo.setErrorMsg("该文档是为加密文档或者已经损坏，暂不支持预览！");
        } else {
            List<String> sheetNames = new ArrayList<>();
            for (int i = 0; i < sheets.size(); i++) {
                Sheet sheetInfo = sheets.get(i);
                String sheetName = sheetInfo.getName();
                boolean isHidden = !sheetInfo.isVisible();
                String hiddenFlag = isHidden ? "_$h1$" : "_$h0$";
                boolean isActive = sheetInfo.isActive();
                String activeFlag = isActive ? "_$a1$" : "_$a0$";
                sheetName = sheetName + hiddenFlag + activeFlag;
                sheetNames.add(sheetName);
            }
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(sheets.size());
            pageInfo.setSheetNames(sheetNames);
        }
        return pageInfo;
    }


    private PageInfo parseWord(String filePath) {
        int pageCount = WordHelper.getPageCount(new File(filePath));
        return getPageInfo(filePath,pageCount);
    }


    private PageInfo parsePPT(String filePath) throws Exception {
        int pageCount = PPTHelper.getSlideCount(new File(filePath));
        return getPageInfo(filePath,pageCount);
    }


    private PageInfo parsePdf(String filePath) throws Exception {
        int pageCount = PdfHelper.getPageCount(filePath);
        return getPageInfo(filePath,pageCount);
    }

    private PageInfo getPageInfo(String filePath,int pageCount) {
        PageInfo pageInfo = new PageInfo();
        if (pageCount < 0) {
            pageInfo.setSuccess(false);
            pageInfo.setPageCount(pageCount);
            pageInfo.setErrorMsg("该文档是为加密文档或者已经损坏，暂不支持预览！");
            log.warn("file:{} get page count is {},may be isn't normal",filePath, pageCount);
        } else {
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(pageCount);
        }
        return pageInfo;
    }


    public static void main(String[] args) throws Exception {

    }
}
