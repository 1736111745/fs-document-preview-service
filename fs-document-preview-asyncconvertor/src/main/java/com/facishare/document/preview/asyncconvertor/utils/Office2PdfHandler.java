package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
import com.facishare.document.preview.common.utils.Office2PdfApiHelper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by liuq on 2017/4/13.
 */
@Component
@Slf4j
public class Office2PdfHandler {
    @Autowired
    Office2PdfApiHelper office2PdfApiHelper;
    @Resource(name = "pdf2HtmlProvider")
    ConvertorQueueProvider pdf2HtmlProvider;
    @Autowired
    PreviewInfoDao previewInfoDao;
    private final ThreadFactory factory =
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("convertHelper-%d").build();
    private final ExecutorService executorService = Executors.newCachedThreadPool(factory);

    public void convertOffice2Pdf(String ea, String path, String filePath) throws Exception {
        String ext = FilenameUtils.getExtension(filePath).toLowerCase();
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        int pageCount = previewInfo.getPageCount();
        if (ext.equals("pdf")) {
            enqueueMultiPagePdf(ea, path, filePath, pageCount);
        } else if (ext.contains("ppt")) {
            for (int i = 0; i < pageCount; i++) {
                final int page = i;
                executorService.submit(() -> {
                    byte[] bytes = office2PdfApiHelper.getPdfBytes(filePath, page);
                    if (bytes != null) {
                        int pageIndex=page+1;
                        String pdfPageFilePath = filePath + "." + pageIndex + ".pdf";
                        log.info("pdfPageFilePath:{}", pdfPageFilePath);
                        try {
                            FileUtils.writeByteArrayToFile(new File(pdfPageFilePath), bytes);
                           enqueue(ea,path,pdfPageFilePath,pageIndex,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else if (ext.contains("doc")) {
            byte[] bytes = office2PdfApiHelper.getPdfBytes(filePath);
            String pdfPageFilePath = filePath + "." + ".pdf";
            FileUtils.writeByteArrayToFile(new File(pdfPageFilePath), bytes);
            enqueueMultiPagePdf(ea, path, pdfPageFilePath, pageCount);
        }
    }

    private void enqueueMultiPagePdf(String ea, String path, String filePath, int pageCount) {
        for (int i = 1; i <= pageCount; i++) {
            enqueue(ea, path, filePath, i, 2);
        }
    }

    private void enqueue(String ea, String path, String filePath, int page, int type) {
        ConvertPdf2HtmlMessage message = new ConvertPdf2HtmlMessage();
        message.setNpath(path);
        message.setFilePath(filePath);
        message.setEa(ea);
        message.setPage(page);
        pdf2HtmlProvider.enqueue(message);
    }

}
