package com.facishare.document.preview.cgi.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.FilenameUtils;

import java.io.*;

/**
 * Created by liuq on 2017/3/25.
 */
public class PdfHelper {


    public static String getPdfData(String pdfFilePath, int page) throws IOException {

        String baseDir = FilenameUtils.getFullPathNoEndSeparator(pdfFilePath);
        String fileName = FilenameUtils.getBaseName(pdfFilePath);
        int pageIndex = page + 1;
        String partFilePath = FilenameUtils.concat(baseDir, fileName + "_" + pageIndex + ".pdf");
        File file = new File(partFilePath);
        if (!file.exists()) {
            splitPDF(new FileInputStream(pdfFilePath), new FileOutputStream(partFilePath), pageIndex, pageIndex);
        }
        return partFilePath;
    }


    private static void splitPDF(InputStream inputStream,
                                 OutputStream outputStream, int fromPage, int toPage) {

        Document document = new Document();
        try {
            PdfReader inputPDF = new PdfReader(inputStream);
            int totalPages = inputPDF.getNumberOfPages();

            // make fromPage equals to toPage if it is greater
            if (fromPage > toPage) {
                fromPage = toPage;
            }
            if (toPage > totalPages) {
                toPage = totalPages;
            }

            // Create a writer for the outputstream
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            PdfContentByte cb = writer.getDirectContent(); // Holds the PDF data
            PdfImportedPage page;

            while (fromPage <= toPage) {
                document.newPage();
                page = writer.getImportedPage(inputPDF, fromPage);
                cb.addTemplate(page, 0, 0);
                fromPage++;
            }
            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen())
                document.close();
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
