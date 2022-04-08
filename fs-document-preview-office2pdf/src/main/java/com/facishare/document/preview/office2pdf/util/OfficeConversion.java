package com.facishare.document.preview.office2pdf.util;

import com.aspose.cells.Workbook;
import com.aspose.slides.Presentation;
import com.aspose.words.SaveFormat;

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

    private static byte[] ConvertXls2Xlsx(byte[] bytes, String path)  {
        String fileString=bytes.toString();
        try {
            Workbook workBook = new Workbook(fileString);
            workBook.save(fileString,com.aspose.cells.SaveFormat.XLSX);
            workBook.dispose();
            return fileString.getBytes();
        } catch (Exception e) {
            //打日志
            return bytes;
        }
    }

    private static byte[] ConvertPpt2Pptx(byte[] bytes, String path) {
        String fileString=bytes.toString();
        try {
            Presentation ppt = new com.aspose.slides.Presentation(fileString);
            ppt.save(fileString, com.aspose.slides.SaveFormat.Pptx);
            return fileString.getBytes();
        }catch (Exception e){
            //打日志
            return bytes;
        }
    }

    private static byte[] ConvertDoc2Docx(byte[] bytes, String path) {
        String fileString=bytes.toString();
        try {
            com.aspose.words.Document doc=new com.aspose.words.Document(fileString);
            doc.save(fileString, SaveFormat.DOCX);
            return fileString.getBytes();
        } catch (Exception e) {
            //打印日志
            return bytes;
        }
    }
}
