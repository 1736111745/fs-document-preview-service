package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
import com.facishare.document.preview.common.utils.OfficeApiHelper;
import com.fxiaoke.log.AuditLog;
import com.fxiaoke.log.BizLogClient;
import com.fxiaoke.log.dto.AuditLogDTO;
import com.fxiaoke.metrics.CounterService;
import com.fxiaoke.pb.Pojo2Protobuf;
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
  private final ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("office-to-pdf-%d").build();
  private final ExecutorService executorService = Executors.newCachedThreadPool(factory);

  public void convertOffice2Pdf(String ea, String path, String filePath, int width) {
    String ext = FilenameUtils.getExtension(filePath).toLowerCase();
    PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path, width);
    int pageCount = previewInfo.getPageCount();
    List<String> dataFilePathList = previewInfo.getFilePathList();
    if (dataFilePathList == null) {
      dataFilePathList = Lists.newArrayList();
    }
    String queryExtension = previewInfo.getPdfConvertType() == 0 ? ".html" : ".png";
    List<Integer> hasNotConvertPageList = Lists.newArrayList();
    for (int i = 1; i < pageCount + 1; i++) {
      if (!dataFilePathList.contains(i + queryExtension)) {
        {
          hasNotConvertPageList.add(i);
        }
      }
    }
    if (ext.equals("pdf")) {
      enqueueMultiPagePdf(ea, path, filePath, hasNotConvertPageList, width, previewInfo.getPdfConvertType());
    } else if (ext.contains("ppt")) {
      int hasNotConvertPageCount = hasNotConvertPageList.size();
      if (hasNotConvertPageCount <= pptMaxPage) {
        log.info("convert ppt to  pdf page by page!");
        for (int i = 0; i < hasNotConvertPageList.size(); i++) {
          final int page = hasNotConvertPageList.get(i);
          executorService.submit(() -> {
            boolean flag;
            long startTime = System.currentTimeMillis();
            AuditLogDTO.AuditLogDTOBuilder builder = AuditLogDTO.builder()
              .appName("document-preview")
              .createTime(startTime)
              .objectIds(String.valueOf(page))
              .extra(path)
              .num(hasNotConvertPageCount);
            try {
              flag = officeApiHelper.convertOffice2Pdf(filePath, page);
              if (flag) {
                builder.status("success");
                counterService.inc("ppt2pdf-success!");
                String pdfPageFilePath = filePath + "." + page + ".pdf";
                enqueue(ea, path, pdfPageFilePath, page, 1, width, previewInfo.getPdfConvertType());
              } else {
                builder.status("fail");
                counterService.inc("ppt2pdf-fail!");
              }
            } catch (IOException e) {
              builder.status("fail").error(e.getMessage());
              counterService.inc("ppt2pdf-fail!");
            }
            AuditLogDTO dto = builder.action("ppt2pdf").ea(ea).cost(System.currentTimeMillis() - startTime).build();
            BizLogClient.send("biz-audit-log", Pojo2Protobuf.toMessage(dto, AuditLog.class).toByteArray());
          });
        }
      } else {
        log.info("convert ppt to  pdf once time!");
        executorService.submit(() -> {
          boolean flag;
          long startTime = System.currentTimeMillis();
          AuditLogDTO.AuditLogDTOBuilder builder = AuditLogDTO.builder()
            .appName("document-preview")
            .createTime(startTime)
            .objectIds(String.valueOf(hasNotConvertPageCount))
            .extra(path)
            .num(hasNotConvertPageCount);
          try {
            flag = officeApiHelper.convertPPT2Pdf(filePath);
            if (flag) {
              builder.status("success");
              counterService.inc("ppt2pdf-success!");
              String pdfPageFilePath = filePath + ".pdf";
              enqueueMultiPagePdf(ea, path, pdfPageFilePath, hasNotConvertPageList, width, previewInfo.getPdfConvertType());
            } else {
              builder.status("fail");
              counterService.inc("ppt2pdf-fail!");
            }
          } catch (IOException e) {
            builder.status("fail").error(e.getMessage());
            counterService.inc("ppt2pdf-fail!");
          }
          AuditLogDTO dto = builder.action("ppt2pdf").ea(ea).cost(System.currentTimeMillis() - startTime).build();
          BizLogClient.send("biz-audit-log", Pojo2Protobuf.toMessage(dto, AuditLog.class).toByteArray());
        });

      }
    } else if (ext.contains("doc")) {
      executorService.submit(() -> {
        boolean flag;
        long startTime = System.currentTimeMillis();
        AuditLogDTO.AuditLogDTOBuilder builder = AuditLogDTO.builder()
          .appName("document-preview")
          .createTime(startTime)
          .objectIds(String.valueOf(hasNotConvertPageList.size()))
          .extra(path)
          .num(hasNotConvertPageList.size());
        try {
          flag = officeApiHelper.convertOffice2Pdf(filePath);
          if (flag) {
            builder.status("success");
            counterService.inc("word2pdf-success!");
            String pdfPageFilePath = filePath + ".pdf";
            enqueueMultiPagePdf(ea, path, pdfPageFilePath, hasNotConvertPageList, width, previewInfo.getPdfConvertType());
          } else {
            builder.status("fail");
            counterService.inc("word2pdf-fail!");
          }
        } catch (IOException e) {
          builder.status("fail").error(e.getMessage());
          counterService.inc("word2pdf-fail!");
        }
        AuditLogDTO dto = builder.action("word2pdf").ea(ea).cost(System.currentTimeMillis() - startTime).build();
        BizLogClient.send("biz-audit-log", Pojo2Protobuf.toMessage(dto, AuditLog.class).toByteArray());
      });
    }
  }

  private void enqueueMultiPagePdf(String ea, String path, String filePath, List<Integer> hasNotConvertPageList, int width, int pdfConvertType) {
    for (int i = 0; i < hasNotConvertPageList.size(); i++) {
      int page = hasNotConvertPageList.get(i);
      enqueue(ea, path, filePath, page, 2, width, pdfConvertType);
    }
  }

  private void enqueue(String ea, String path, String filePath, int page, int type, int width, int pdfConvertType) {
    ConvertPdf2HtmlMessage message = new ConvertPdf2HtmlMessage();
    message.setNpath(path);
    message.setFilePath(filePath);
    message.setEa(ea);
    message.setPage(page);
    message.setType(type);
    message.setPageWidth(width);
    message.setPdfConvertType(pdfConvertType);
    pdf2HtmlProvider.pdf2html(message);
  }

}
