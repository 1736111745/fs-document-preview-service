package com.facishare.document.preview.cgi.convertor;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by liuq on 16/9/9.
 */
public class PDFConvertor implements IDocConvertor {
    private static final Logger LOG = LoggerFactory.getLogger(PDFConvertor.class);

    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {
//        ConvertorPool.ConvertorObject convertobj = ConvertorPool.getInstance().getConvertor();
//        try {
//            LOG.info("begin get IPICConvertor");
//            IPICConvertor ipicConvertor = convertobj.convertor.convertPdftoPic(filePath);
//            LOG.info("end get IPICConvertor");
//            int resultcode = ipicConvertor.resultCode();
//            if (resultcode == 0) {
//                String fileName = (page1 + 1) + ".png";
//                String pngFilePath = baseDir + "/" + fileName;
//                LOG.info("begin get image,folder:{},page:{}", baseDir, page1);
//                int code = ipicConvertor.convertToPNG(page1, page2, 2f, baseDir);
//                LOG.info("end get image,folder:{},code:{},page:{}", baseDir, code, page1);
//                ipicConvertor.close();
//                File file = new File(pngFilePath);
//                if (file.exists()) {
//                    Thumbnails.of(file).scale(0.5).outputFormat("png").toFile(file);
//                    return FilenameUtils.getBaseName(baseDir) + "/" + fileName;
//                } else {
//                    return "";
//                }
//            } else
//                return "";
//        } catch (Exception e) {
//            LOG.error("error info", e);
//            return "";
//        } finally {
//            ConvertorPool.getInstance().returnConvertor(convertobj);
//        }
        String fileName = (page1 + 1) + ".pdf";
        String pdfFilePath = baseDir + "/" + fileName;
        File sourceFile = new File(filePath);
        File aimFile = new File(pdfFilePath);
        splitPDF(filePath, pdfFilePath, page1 + 1, page2 + 1);
        return FilenameUtils.getBaseName(baseDir) + "/" + fileName;
    }

    private static void splitPDF(String pdfFile, String newFile, int from, int end) {
        Document document;
        PdfCopy copy;
        try {

            PdfReader reader = new PdfReader(pdfFile);
            int n = reader.getNumberOfPages();
            if (end == 0) {
                end = n;
            }
            ArrayList<String> savepaths = new ArrayList<>();
            String staticpath = pdfFile.substring(0, pdfFile.lastIndexOf("\\") + 1);
            String savepath = staticpath + newFile;
            savepaths.add(savepath);
            document = new Document(reader.getPageSize(1));
            copy = new PdfCopy(document, new FileOutputStream(savepaths.get(0)));
            document.open();
            for (int j = from; j <= end; j++) {
                document.newPage();
                PdfImportedPage page = copy.getImportedPage(reader, j);
                copy.addPage(page);
            }
            document.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
