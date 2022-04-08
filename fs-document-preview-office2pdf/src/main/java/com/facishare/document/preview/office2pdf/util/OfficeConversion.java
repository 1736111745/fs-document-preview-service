package com.facishare.document.preview.office2pdf.util;

import com.aspose.cells.Workbook;
import com.aspose.slides.Presentation;
import com.aspose.words.SaveFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class OfficeConversion {
    public static byte[] DoConvertWithAspose(byte[] bytes, String path) {
        try {
            if(!FileEncryptChecker.checkIsEncrypt(bytes,path)){
                String fileSuffix=path.substring(path.lastIndexOf(".")).toLowerCase();
                switch (fileSuffix){
                    case ".doc":
                    case ".docx":return ConvertDoc2Docx(bytes,path);
                    case ".ppt":
                    case ".pptx":return ConvertPpt2Pptx(bytes,path);
                    case ".xls": return ConvertXls2Xlsx(bytes,path);
                    default:return  bytes;
                }
            }
        } catch (Exception e) {

        }
        return bytes;
    }

    private static byte[] ConvertXls2Xlsx(byte[] data, String path)  {
        ByteArrayInputStream fileInputStream=new ByteArrayInputStream(data);
        ByteArrayOutputStream fileOutputStrem=new ByteArrayOutputStream();
        try {
            Workbook workBook = new Workbook(fileInputStream);
            workBook.save(fileOutputStrem,com.aspose.cells.SaveFormat.XLSX);
            workBook.dispose();
            return fileOutputStrem.toByteArray();
        } catch (Exception e) {
            //打日志
            return fileOutputStrem.toByteArray();
        }
    }

    private static byte[] ConvertPpt2Pptx(byte[] data, String path) {
        ByteArrayInputStream fileInputStream=new ByteArrayInputStream(data);
        ByteArrayOutputStream fileOutputStrem=new ByteArrayOutputStream();
        try {
            Presentation ppt = new com.aspose.slides.Presentation(fileInputStream);
            ppt.save(fileOutputStrem, com.aspose.slides.SaveFormat.Pptx);
            return fileOutputStrem.toByteArray();
        }catch (Exception e){
            //打日志
            return fileOutputStrem.toByteArray();
        }
    }

    private static byte[] ConvertDoc2Docx(byte[] data, String path) {
        ByteArrayInputStream fileInputStream=new ByteArrayInputStream(data);
        ByteArrayOutputStream fileOutputStrem=new ByteArrayOutputStream();
        try {
            com.aspose.words.Document doc=new com.aspose.words.Document(fileInputStream);
            doc.save(fileOutputStrem, SaveFormat.DOCX);
            return fileOutputStrem.toByteArray();
        } catch (Exception e) {
            //打印日志
            return fileOutputStrem.toByteArray();
        }
    }
}
