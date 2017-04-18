package com.facishare.document.preview.common.utils.aspose;


import com.aspose.pdf.SaveFormat;
import com.aspose.words.PdfSaveOptions;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuq on 2017/4/18.
 */
@Component
@Slf4j
public class Office2PdfHelper {
    public Office2PdfHelper() {
        LicenceHelper.setAllLicence();
    }

    /**
     * 转换word为pdf
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public byte[] convertWord2Pdf(String filePath) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("begin convert word to pdf!");
        com.aspose.words.Document document = new com.aspose.words.Document(filePath);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
        document.save(outputStream, pdfSaveOptions);
        stopwatch.stop();
        log.info("end convert word to pdf,file size:{},cost:{}ms", FileUtils.sizeOf(new File(filePath)), stopwatch.elapsed(TimeUnit.SECONDS));
        return outputStream.toByteArray();
    }

    /**
     * 转换ppt中的某一页为pdf
     * @param filePath  文件路径
     * @return pdf的二进制
     * @throws Exception
     */
    public byte[] convertPpt2Pdf(String filePath) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("begin convert ppt to pdf!");
        com.aspose.slides.Presentation presentation = new com.aspose.slides.Presentation(filePath);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        presentation.save(outputStream, SaveFormat.Pdf);
        stopwatch.stop();
        log.info("end convert word to pdf,file size:{},cost:{}s", FileUtils.sizeOf(new File(filePath)), stopwatch.elapsed(TimeUnit.SECONDS));
        return outputStream.toByteArray();
    }

    public static void main(String[] args) throws Exception {
    }
}

