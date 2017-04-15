package com.facishare.document.preview.asyncconvertor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.facishare.common.rocketmq.AutoConfRocketMQProcessor;
import com.facishare.document.preview.asyncconvertor.utils.Office2PdfHandler;
import com.facishare.document.preview.common.dao.Office2PdfTaskDao;
import com.facishare.document.preview.common.model.ConvertMessage;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
import com.fxiaoke.metrics.CounterService;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * Created by liuq on 2017/3/9.
 */

@Slf4j
@Component
public class Office2PdfProcessor {
    @Autowired
    private CounterService counterService;
    @Autowired
    Office2PdfHandler office2PdfHandler;
    @Autowired
    Office2PdfTaskDao office2PdfTaskDao;
    @Resource(name = "pdf2HtmlProvider")
    ConvertorQueueProvider pdf2HtmlProvider;
    private AutoConfRocketMQProcessor autoConfRocketMQProcessor;
    private static final String KEY_NAME_SERVER = "NAMESERVER";
    private static final String KEY_GROUP = "GROUP_CONSUMER";
    private static final String KEY_TOPICS = "TOPICS";

    @ReloadableProperty("office2pdf_mq_config_name")
    private String configName = "fs-dps-mq-office2pdf";

    public void init() {
        log.info("begin consumer office2pdf queue!");
        autoConfRocketMQProcessor = new AutoConfRocketMQProcessor(configName, KEY_NAME_SERVER, KEY_GROUP, KEY_TOPICS, (MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            list.forEach((MessageExt messageExt) -> {
                ConvertMessage convertorMessage = ConvertMessage.builder().build();
                convertorMessage.fromProto(messageExt.getBody());
                try {
                    office2Pdf(convertorMessage);
                } catch (Exception ex) {
                    log.error("do convert happened exception,params:{}", ex, JSON.toJSONString(convertorMessage));
                }
            });
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        autoConfRocketMQProcessor.init();
    }

    private void office2Pdf(ConvertMessage office2PdfMessage) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("begin do convert,params:{}", JSON.toJSONString(office2PdfMessage));
        String ea = office2PdfMessage.getEa();
        String path = office2PdfMessage.getNpath();
        String filePath = office2PdfMessage.getFilePath();
        int page = office2PdfMessage.getPage();
        int status = office2PdfTaskDao.getTaskStatus(ea, path, page);
        log.info("status:{}",status);
        if (status == 0) {
            office2PdfTaskDao.beginExecute(ea, path, page);
            String ext = FilenameUtils.getExtension(path);
            String dataFilePath = office2PdfHandler.ConvertOffice2Pdf(filePath, page);
            File dataFile = new File(dataFilePath);
            String indexName = ext.contains("doc") ? "convert-word2pdf" : "convert-ppt2pdf";
            if (dataFile.exists()) {
                counterService.inc(indexName + "--ok");
                ConvertMessage pdf2HtmlMessage=ConvertMessage.builder().ea(ea).filePath(dataFilePath).npath(path).page(page).build();
                pdf2HtmlProvider.enqueue(pdf2HtmlMessage);
                office2PdfTaskDao.executeSuccess(ea, path, page);
            } else {
                counterService.inc(indexName + "--fail");
                office2PdfTaskDao.executeFail(ea, path, page);
            }
        }
        stopWatch.stop();
        log.info("end do convert,params:{},cost:{}", JSON.toJSONString(office2PdfMessage), stopWatch.getTime());
    }
}
