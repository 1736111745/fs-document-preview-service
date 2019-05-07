package com.facishare.document.preview.asyncconvertor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.facishare.common.rocketmq.AutoConfRocketMQProcessor;
import com.facishare.document.preview.asyncconvertor.utils.Pdf2HtmlHandler;
import com.facishare.document.preview.asyncconvertor.utils.Pdf2ImageHandler;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.fxiaoke.metrics.CounterService;
import com.github.autoconf.ConfigFactory;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * Created by liuq on 2017/3/9.
 */

@Slf4j
@Component
public class Pdf2HtmlProcessor {
  @Autowired
  Pdf2HtmlHandler pdf2HtmlHandler;
  @Autowired
  Pdf2ImageHandler pdf2ImageHandler;
  @Autowired
  PreviewInfoDao previewInfoDao;
  @Autowired
  private CounterService counterService;
  private AutoConfRocketMQProcessor autoConfRocketMQProcessor;
  private static final String KEY_NAME_SERVER = "NAMESERVER";
  private static final String KEY_GROUP = "GROUP_CONSUMER";
  private static final String KEY_TOPICS = "TOPICS";
  @ReloadableProperty("pdf2html_mq_config_name")
  private String configName = "fs-dps-mq-pdf2html";
  /**
   * 限定并发数量，避免占用cpu过多
   */
  private Semaphore limiter;

  public void init() {
    log.info("begin consumer pdf2html queue!");
    ConfigFactory.getConfig("fs-dps-config", config -> limiter = new Semaphore(config.getInt("pdf2html-limiter", 20)));
    autoConfRocketMQProcessor = new AutoConfRocketMQProcessor(configName,
      KEY_NAME_SERVER,
      KEY_GROUP,
      KEY_TOPICS,
      (MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
        final Semaphore semaphore = limiter;
        list.forEach((MessageExt messageExt) -> {
          ConvertPdf2HtmlMessage message = new ConvertPdf2HtmlMessage();
          message.fromProto(messageExt.getBody());
          try {
            semaphore.acquire();
            doConvert(message);
          } catch (Exception ex) {
            log.error("do convert happened exception, params:{}, ", JSON.toJSONString(message), ex);
          } finally {
            semaphore.release();
          }
        });
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
      });
    autoConfRocketMQProcessor.init();
  }

  private void doConvert(ConvertPdf2HtmlMessage message) throws IOException, InterruptedException {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    log.info("begin do convert,params:{}", JSON.toJSONString(message));
    String ea = message.getEa();
    String path = message.getNpath();
    String filePath = message.getFilePath();
    int page = message.getPage();
    String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
    String resultFilePath = basedDir + "/" + page + ".html";
    if (message.getPdfConvertType() == 1) {
      resultFilePath = basedDir + "/" + page + ".png";
    }
    if (new File(resultFilePath).exists()) {
      log.info("data file:{} exists!not need convert!", resultFilePath);
      return;
    }
    String dataFilePath = message.getPdfConvertType() == 0 ? pdf2HtmlHandler.doConvert(message) : pdf2ImageHandler.doConvert(message);
    if (!Strings.isNullOrEmpty(dataFilePath)) {
      counterService.inc("convert-pdf2html-ok");
      previewInfoDao.savePreviewInfo(ea, path, dataFilePath, message.getPageWidth());
    } else {
      counterService.inc("convert-pdf2html-fail");
    }
    stopWatch.stop();
    log.info("end do convert,params:{},cost:{}ms", JSON.toJSONString(message), stopWatch.getTime());
  }

  public static void main(String[] args) {


  }
}
