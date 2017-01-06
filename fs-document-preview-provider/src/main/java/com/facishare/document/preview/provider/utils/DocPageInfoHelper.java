package com.facishare.document.preview.provider.utils;

import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.common.utils.DocType;
import com.facishare.document.preview.common.utils.DocTypeHelper;
import com.facishare.document.preview.provider.convertor.ConvertorHelper;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4jyz.Attribute;
import org.dom4jyz.Document;
import org.dom4jyz.DocumentHelper;
import org.dom4jyz.Element;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


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
        PageInfo pageInfo = new PageInfo();
        InputStream ins = null;
        Workbook workbook = null;
        try {
            ins = new ByteArrayInputStream(data);
            workbook = StreamingReader.builder().open(ins);
            int pageCount = workbook.getNumberOfSheets();
            List<String> sheetNames = new ArrayList<>();
            for (int i = 0; i < pageCount; i++) {
                Sheet xssfSheet = workbook.getSheetAt(i);
                String sheetName = xssfSheet.getSheetName();
                boolean isHidden = false;
                String hiddenFlag = isHidden ? "_$h1$" : "";
                boolean isActive = i==0;
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
        finally {
            if (ins != null)
                ins.close();
            if (workbook != null)
                workbook.close();
        }
        return pageInfo;
    }

    private static PageInfo parseExcel2003(String filePath, byte[] data) throws Exception {
        PageInfo pageInfo = new PageInfo();
        try {
            @Cleanup InputStream input = new ByteArrayInputStream(data);
            @Cleanup HSSFWorkbook hs = new HSSFWorkbook(input);
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

    public static void main(String[] args) throws Exception {
        String file = "/Users/liuq/Downloads/kylfklwv.xlsx";
        ZipFile zf = new ZipFile(file);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        String workBookXml = "";
        while ((ze = zin.getNextEntry()) != null) {
            if (ze.isDirectory()) {
                //为空的文件夹什么都不做
            } else {
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
        }
        System.out.println(workBookXml);
        Document document= DocumentHelper.parseText(workBookXml);
        Element root = document.getRootElement();//获取根节点
        getNodes(root);//从根节点开始遍历所有节点
    }

    private static void getNodes(Element node){
        System.out.println("--------------------");

        //当前节点的名称、文本内容和属性
        System.out.println("当前节点名称："+node.getName());//当前节点名称
        System.out.println("当前节点的内容："+node.getTextTrim());//当前节点名称
        List<Attribute> listAttr=node.attributes();//当前节点的所有属性的list
        for(Attribute attr:listAttr){//遍历当前节点的所有属性
            String name=attr.getName();//属性名称
            String value=attr.getValue();//属性的值
            System.out.println("属性名称："+name+"属性值："+value);
        }
        //递归遍历当前节点所有的子节点
        List<Element> listElement=node.elements();//所有一级子节点的list
        for(Element e:listElement){//遍历所有一级子节点
            getNodes(e);//递归
        }
    }
}
