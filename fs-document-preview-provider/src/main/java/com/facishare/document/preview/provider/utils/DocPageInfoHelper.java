package com.facishare.document.preview.provider.utils;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.common.utils.DocType;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import com.facishare.document.preview.provider.convertor.ConvertorHelper;
import com.facishare.document.preview.provider.model.SheetInfo;
import com.fxiaoke.excel.Excel;
import com.fxiaoke.excel.Sheet;
import com.google.common.collect.Lists;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.dom4jyz.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

//import org.apache.poi.ss.usermodel.Workbook;


/**
 * Created by liuq on 16/9/7.
 */
@Slf4j
@Component
public class DocPageInfoHelper {
    public PageInfo GetPageInfo(String filePath) throws Exception {
        //word只有排版后才知道页码数，excel，ppt，pdf的页码是固定的。
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        byte[] data = FileUtils.readFileToByteArray(new File(filePath));
        log.info("begin get page count,filePath:{}", filePath);
        try {
            DocType docType = DocTypeHelper.getDocType(filePath);
            switch (docType) {
                case Word:
                    return pareWord(filePath);
                case Excel:
                    return parseExcel(filePath, data);
                case PPT:
                    return parsePPT(filePath);
                case PDF:
                    return parsePDF(filePath, data);
                default:
                    return new PageInfo();
            }
        } finally {
            stopWatch.stop();
            log.info("get page count done,filePath:{},cost:{}", filePath, stopWatch.getTime() + "ms");
        }
    }


    private int checkFileVersion(byte[] data) throws IOException {
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

    private PageInfo parsePPT(String filePath) throws Exception {
        return ConvertorHelper.getWordPageCount(filePath);
    }


    private PageInfo parseExcel(String filePath, byte[] data) throws Exception {
        int version = checkFileVersion(data);
        return version == 2003 ? parseExcel2003(filePath, data) : parseExcel2007(filePath, data);
    }


    private PageInfo parsePDF(String filePath, byte[] data) throws IOException {
        PageInfo pageInfo = new PageInfo();
        try {
            @Cleanup InputStream input = new ByteArrayInputStream(data);
            @Cleanup PDDocument doc = PDDocument.load(input);
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(doc.getNumberOfPages());
        } catch (Exception ex) {
            pageInfo.setSuccess(false);
            log.error("parse excel happened error,path:{}!", filePath, ex);
        }
        return pageInfo;
    }

    private PageInfo pareWord(String filePath) throws Exception {
        return ConvertorHelper.getWordPageCount(filePath);
    }

    private static PageInfo parseExcel2007(String filePath, byte[] data) throws Exception {
        List<SheetInfo> sheetInfos = getSheetsForXlsx(filePath);
        return convertSheetInfos2PageInfo(sheetInfos);
    }

    private static PageInfo parseExcel2003(String filePath, byte[] data) throws Exception {
        List<SheetInfo> sheetInfos = getSheetsForXls(filePath);
        return convertSheetInfos2PageInfo(sheetInfos);
    }

    private static PageInfo convertSheetInfos2PageInfo(List<SheetInfo> sheetInfos) {
        PageInfo pageInfo = new PageInfo();
        if (sheetInfos == null) {
            pageInfo.setSuccess(false);
            pageInfo.setErrorMsg("该文档是为加密文档或者已经损坏，暂不支持预览！");
        } else {
            List<String> sheetNames = new ArrayList<>();
            for (int i = 0; i < sheetInfos.size(); i++) {
                SheetInfo sheetInfo = sheetInfos.get(i);
                String sheetName = sheetInfo.getSheetName();
                boolean isHidden = sheetInfo.isHidden();
                String hiddenFlag = isHidden ? "_$h1$" : "_$h0$";
                boolean isActive = sheetInfo.isActvie();
                String activeFlag = isActive ? "_$a1$" : "_$a0$";
                sheetName = sheetName + hiddenFlag + activeFlag;
                sheetNames.add(sheetName);
            }
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(sheetInfos.size());
            pageInfo.setSheetNames(sheetNames);
        }
        return pageInfo;
    }

    private static List<SheetInfo> getSheetsForXls(String file) throws Exception {
        try {
            Excel excel = new Excel(new File(file));
            List<Sheet> sheets = excel.parse();
            List<SheetInfo> sheetInfos = Lists.newArrayList();
            for (Sheet sheet : sheets) {
                SheetInfo sheetInfo = SheetInfo.builder().isHidden(sheet.getState()==1).sheetName(sheet.getName()).build();
                sheetInfos.add(sheetInfo);
            }
            sheetInfos.get(0).setActvie(true);
            return sheetInfos;
        } catch (Exception e) {
            return null;
        }
    }

    private static List<SheetInfo> getSheetsForXlsx(String file) throws IOException, DocumentException {
        try {
            List<SheetInfo> sheetInfos = Lists.newArrayList();
            ZipFile zf = new ZipFile(file);
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            ZipInputStream zin = new ZipInputStream(in);
            ZipEntry ze;
            String workBookXml = "";
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.getName().equals("xl/workbook.xml")) {
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(zf.getInputStream(ze), "utf-8"));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            workBookXml += line;
                        }
                    } catch (IOException e) {
                    } finally {
                        reader.close();
                    }
                    break;
                }
            }
            in.close();
            zin.close();
            Document document = DocumentHelper.parseText(workBookXml);
            Element root = document.getRootElement();//获取根节点
            List sheets = root.element("sheets").elements();
            for (Object e : sheets) {
                Element sheet = (Element) e;
                String sheetName = sheet.attributeValue("name");
                Attribute state = sheet.attribute("state");
                boolean isHidden = state != null && state.getValue().equals("hidden") ? true : false;
                SheetInfo sheetInfo = SheetInfo.builder().sheetName(sheetName).isHidden(isHidden).build();
                sheetInfos.add(sheetInfo);
            }
            int activeIndex = 0;
            Element bookViews = root.element("bookViews");
            if (bookViews != null) {
                Element workbookView = bookViews.element("workbookView");
                if (workbookView != null) {
                    Attribute activeTab = workbookView.attribute("activeTab");
                    if (activeTab != null) {
                        activeIndex = NumberUtils.toInt(activeTab.getValue(), 0);
                    }
                }
            }
            sheetInfos.get(activeIndex).setActvie(true);
            return sheetInfos;
        } catch (Exception e) {
            log.error("parse excel happened error,path:{}!", file, e);
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String file = "/Users/liuq/Downloads/kylfklwv.xls";
        List<SheetInfo> sheetInfos = getSheetsForXls(file);
        stopWatch.stop();
        System.out.println(stopWatch.getTime() + "ms");
        String json = JSON.toJSONString(sheetInfos);
        System.out.println(json);

    }

}
