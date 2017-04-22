package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.api.model.arg.ConvertDocArg;
import com.facishare.document.preview.api.service.DocConvertService;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
import com.facishare.document.preview.common.utils.OfficeApiHelper;
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
    OfficeApiHelper officeApiHelper;
    @Resource(name = "pdf2HtmlProvider")
    ConvertorQueueProvider pdf2HtmlProvider;
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    DocConvertService docConvertService;
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
            long fileSize = FileUtils.sizeOf(new File(filePath));
            if (fileSize > 1024 * 1024 * 10) {
                executorService.submit(() -> {
                    ConvertDocArg convertDocArg = ConvertDocArg.builder().originalFilePath(filePath).path(path).type(3).build();
                    try {
                        String pdfPageFilePath = docConvertService.convertDoc(convertDocArg).getDataFilePath();
                        counterService.inc("ppt2pdf-success!");
                        enqueueMultiPagePdf(ea, path, pdfPageFilePath, hasNotConvertPageList);
                    } catch (Exception e) {
                        counterService.inc("word2pdf-fail!");
                    }
                });
            } else {
                executorService.submit(() -> {
                    boolean flag = officeApiHelper.convertOffice2Pdf(path, filePath);
                    if (flag) {
                        counterService.inc("ppt2pdf-success!");
                        String pdfPageFilePath = filePath + ".pdf";
                        enqueueMultiPagePdf(ea, path, pdfPageFilePath, hasNotConvertPageList);
                    } else {
                        counterService.inc("word2pdf-fail!");
                    }
                });
            }
        } else if (ext.contains("doc")) {
            executorService.submit(() -> {
                boolean flag = officeApiHelper.convertOffice2Pdf(path, filePath);
                if (flag) {
                    counterService.inc("word2pdf-success!");
                    String pdfPageFilePath = filePath + ".pdf";
                    enqueueMultiPagePdf(ea, path, pdfPageFilePath, hasNotConvertPageList);
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
