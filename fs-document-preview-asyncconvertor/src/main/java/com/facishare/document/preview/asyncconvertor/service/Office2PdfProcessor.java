package com.facishare.document.preview.asyncconvertor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.facishare.common.rocketmq.AutoConfRocketMQProcessor;
import com.facishare.document.preview.asyncconvertor.utils.Office2PdfHandler;
import com.facishare.document.preview.common.dao.Office2PdfTaskDao;
import com.facishare.document.preview.common.model.ConvertMessageBase;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liuq on 2017/3/9.
 */

@Slf4j
@Component
public class Office2PdfProcessor {
    @Autowired
    Office2PdfHandler office2PdfHandler;
    @Autowired
    Office2PdfTaskDao office2PdfTaskDao;
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
                ConvertMessageBase convertorMessage = new ConvertMessageBase();
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

    private void office2Pdf(ConvertMessageBase office2PdfMessage) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("begin do convert,params:{}", JSON.toJSONString(office2PdfMessage));
        String ea = office2PdfMessage.getEa();
        String path = office2PdfMessage.getNpath();
        String filePath = office2PdfMessage.getFilePath();
        int status = office2PdfTaskDao.getTaskStatus(ea, path);
        if (status == 0) {
            office2PdfTaskDao.beginExecute(ea, path);
            office2PdfHandler.convertOffice2Pdf(ea, path, filePath);
        }
    }
}
