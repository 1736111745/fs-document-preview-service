package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
import com.facishare.document.preview.common.utils.OfficeApiHelper;
import com.fxiaoke.metrics.CounterService;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
  private CounterService counterService;
  @ReloadableProperty("pptMaxPage")
  private int pptMaxPage = 20;
  private final ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true)
                                                                  .setNameFormat("office-to-pdf-%d")
                                                                  .build();
  private final ExecutorService executorService = Executors.newCachedThreadPool(factory);

  public void convertOffice2Pdf(String ea, String path, String filePath, int width) throws Exception {
    String ext = FilenameUtils.getExtension(filePath).toLowerCase();
    PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path,width);
    int pageCount = previewInfo.getPageCount();
    List<String> dataFilePathList = previewInfo.getFilePathList();
    if (dataFilePathList == null) {
      dataFilePathList = Lists.newArrayList();
    }
    List<Integer> hasNotConvertPageList = Lists.newArrayList();
    for (int i = 1; i < pageCount + 1; i++) {
      if (!dataFilePathList.contains(i + ".html")) {
        {
          hasNotConvertPageList.add(i);
        }
      }
    }
    if (ext.equals("pdf")) {
      enqueueMultiPagePdf(ea, path, filePath, hasNotConvertPageList, width);
    } else if (ext.contains("ppt")) {
      int hasNotConvertPageCount = hasNotConvertPageList.size();
      if (hasNotConvertPageCount <= pptMaxPage) {
        log.info("convert ppt to  pdf page by page!");
        for (int i = 0; i < hasNotConvertPageList.size(); i++) {
          final int page = hasNotConvertPageList.get(i);
          executorService.submit(() -> {
            boolean flag = false;
            try {
              flag = officeApiHelper.convertOffice2Pdf(filePath, page);
              if (flag) {
                counterService.inc("ppt2pdf-success!");
                String pdfPageFilePath = filePath + "." + page + ".pdf";
                enqueue(ea, path, pdfPageFilePath, page, 1, width);
              } else {
                counterService.inc("ppt2pdf-fail!");
              }
            } catch (IOException e) {
              counterService.inc("ppt2pdf-fail!");
            }

          });
        }
      } else {
        log.info("convert ppt to  pdf once time!");
        executorService.submit(() -> {
          boolean flag;
          try {
            flag = officeApiHelper.convertPPT2Pdf(filePath);
            if (flag) {
              counterService.inc("ppt2pdf-success!");
              String pdfPageFilePath = filePath + ".pdf";
              enqueueMultiPagePdf(ea, path, pdfPageFilePath, hasNotConvertPageList, width);
            } else {
              counterService.inc("ppt2pdf-fail!");
            }
          } catch (IOException e) {
            counterService.inc("ppt2pdf-fail!");
          }

        });

      }
    } else if (ext.contains("doc")) {
      executorService.submit(() -> {
        boolean flag;
        try {
          flag = officeApiHelper.convertOffice2Pdf(filePath);
          if (flag) {
            counterService.inc("word2pdf-success!");
            String pdfPageFilePath = filePath + ".pdf";
            enqueueMultiPagePdf(ea, path, pdfPageFilePath, hasNotConvertPageList, width);
          } else {
            counterService.inc("word2pdf-fail!");
          }
        } catch (IOException e) {
          counterService.inc("word2pdf-fail!");
        }
      });
    }
  }

  private void enqueueMultiPagePdf(String ea,
                                   String path,
                                   String filePath,
                                   List<Integer> hasNotConvertPageList,
                                   int width) {
    for (int i = 0; i < hasNotConvertPageList.size(); i++) {
      int page = hasNotConvertPageList.get(i);
      enqueue(ea, path, filePath, page, 2, width);
    }
  }

  private void enqueue(String ea, String path, String filePath, int page, int type, int width) {
    ConvertPdf2HtmlMessage message = new ConvertPdf2HtmlMessage();
    message.setNpath(path);
    message.setFilePath(filePath);
    message.setEa(ea);
    message.setPage(page);
    message.setType(type);
    message.setPageWidth(width);
    pdf2HtmlProvider.pdf2html(message);
  }

}
