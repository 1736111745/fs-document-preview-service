package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
import com.facishare.document.preview.common.utils.Office2PdfApiHelper;
import com.fxiaoke.metrics.CounterService;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
    @Autowired
    private CounterService counterService;
    private final ThreadFactory factory =
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("office-to-pdf-%d").build();
    private final ExecutorService executorService = Executors.newCachedThreadPool(factory);

    public void convertOffice2Pdf(String ea, String path, String filePath) throws Exception {
        String ext = FilenameUtils.getExtension(filePath).toLowerCase();
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        int pageCount = previewInfo.getPageCount();
        List<String> dataFilePathList = previewInfo.getFilePathList();
        if (dataFilePathList == null)
            dataFilePathList = Lists.newArrayList();
        List<Integer> hasNotConvertPageList = Lists.newArrayList();
        for (int i = 1; i < pageCount + 1; i++) {
            if (!dataFilePathList.contains(i + ".html")) {
                {
                    hasNotConvertPageList.add(i);
                }
            }
        }
        log.info("hasNotConvertPageList:{}", hasNotConvertPageList);
        if (ext.equals("pdf")) {
            enqueueMultiPagePdf(ea, path, filePath, hasNotConvertPageList);
        } else if (ext.contains("ppt")) {
            for (int i = 0; i < hasNotConvertPageList.size(); i++) {
                final int page = hasNotConvertPageList.get(i);
                executorService.submit(() -> {
                    byte[] bytes = office2PdfApiHelper.getPdfBytes(path, filePath, page);
                    if (bytes != null) {
                        counterService.inc("ppt2pdf-success!");
                        int pageIndex = page + 1;
                        String pdfPageFilePath = filePath + "." + pageIndex + ".pdf";
                        log.info("pdfPageFilePath:{}", pdfPageFilePath);
                        try {
                            FileUtils.writeByteArrayToFile(new File(pdfPageFilePath), bytes);
                            enqueue(ea, path, pdfPageFilePath, pageIndex, 1);
                        } catch (IOException e) {
                            log.warn("save office2pdf fail,path:{},page:{}", path, pageIndex, e);
                        }
                    } else
                        counterService.inc("ppt2pdf-fail!");
                });
            }
        } else if (ext.contains("doc")) {
            executorService.submit(() -> {
                byte[] bytes = office2PdfApiHelper.getPdfBytes(path, filePath);
                if (bytes != null) {
                    counterService.inc("word2pdf-success!");
                    String pdfPageFilePath = filePath + ".pdf";
                    try {
                        FileUtils.writeByteArrayToFile(new File(pdfPageFilePath), bytes);
                        enqueueMultiPagePdf(ea, path, pdfPageFilePath, hasNotConvertPageList);
                    } catch (IOException e) {
                        log.warn("save office2pdf fail,path:{}", path, e);
                    }
                } else {
                    counterService.inc("word2pdf-fail!");
                }
            });
        }
    }

    private void enqueueMultiPagePdf(String ea, String path, String filePath, List<Integer> hasNotConvertPageList) {
        for (int i = 0; i < hasNotConvertPageList.size(); i++) {
            int page = hasNotConvertPageList.get(i);
            enqueue(ea, path, filePath, page, 2);
        }
    }

    private void enqueue(String ea, String path, String filePath, int page, int type) {
        ConvertPdf2HtmlMessage message = new ConvertPdf2HtmlMessage();
        message.setNpath(path);
        message.setFilePath(filePath);
        message.setEa(ea);
        message.setPage(page);
        message.setType(type);
        pdf2HtmlProvider.pdf2html(message);
    }

}
