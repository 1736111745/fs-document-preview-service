package com.facishare.document.preview.asyncconvertor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.facishare.common.rocketmq.AutoConfRocketMQProcessor;
import com.facishare.document.preview.asyncconvertor.utils.Pdf2HtmlHandler;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.fxiaoke.metrics.CounterService;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuq on 2017/3/9.
 */

@Slf4j
@Component
public class Pdf2HtmlProcessor {
    @Autowired
    Pdf2HtmlHandler pdf2HtmlHandler;
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

    public void init() {
        log.info("begin consumer pdf2html queue!");
        autoConfRocketMQProcessor = new AutoConfRocketMQProcessor(configName, KEY_NAME_SERVER, KEY_GROUP, KEY_TOPICS, (MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            list.forEach((MessageExt messageExt) -> {
                ConvertPdf2HtmlMessage message = new ConvertPdf2HtmlMessage();
                message.fromProto(messageExt.getBody());
                try {
                    doConvert(message);
                } catch (Exception ex) {
                    log.error("do convert happened exception,params:{}", ex, JSON.toJSONString(message));
                }
            });
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        autoConfRocketMQProcessor.init();
    }

    private void doConvert(ConvertPdf2HtmlMessage convertorMessage) throws InterruptedException, IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("begin do convert,params:{}", JSON.toJSONString(convertorMessage));
        String ea = convertorMessage.getEa();
        String path = convertorMessage.getNpath();
        String filePath=convertorMessage.getFilePath();
        int page=convertorMessage.getPage();
        String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String htmlFilePath=basedDir+"/"+page+".html";
        if(new File(htmlFilePath).exists()) {
            log.info("data file:{} exsits!not need convert!", htmlFilePath);
            return;
        }
        String dataFilePath = pdf2HtmlHandler.doConvert(convertorMessage);
        if (!Strings.isNullOrEmpty(dataFilePath)) {
            counterService.inc("convert-pdf2html-ok");
            previewInfoDao.savePreviewInfo(ea, path, dataFilePath);
        } else {
            counterService.inc("convert-pdf2html-fail");
        }
        stopWatch.stop();
        log.info("end do convert,params:{},cost:{}ms", JSON.toJSONString(convertorMessage), stopWatch.getTime());
    }
}
