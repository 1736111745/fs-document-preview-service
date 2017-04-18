package com.facishare.document.preview.common.utils.aspose;


import com.aspose.words.PdfSaveOptions;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by liuq on 2017/4/18.
 */
public class Office2PdfHelper {
    public Office2PdfHelper() {
        LicenceHelper.setAllLicence();
    }

    public byte[] convertWord2Pdf(String filePath) throws Exception {
        com.aspose.words.Document document = new com.aspose.words.Document(filePath);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
        document.save(outputStream, pdfSaveOptions);
        return outputStream.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        Office2PdfHelper office2PdfHelper=new Office2PdfHelper();
        byte[] bytes=office2PdfHelper.convertWord2Pdf("/Users/liuq/Downloads/渠道体系修改10月19日.docx");
        FileUtils.writeByteArrayToFile(new File("/Users/liuq/Downloads/lll.pdf"),bytes);
    }
}

