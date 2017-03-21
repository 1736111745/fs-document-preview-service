package com.facishare.document.preview.asyncconvertor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.facishare.common.rocketmq.AutoConfRocketMQProcessor;
import com.facishare.document.preview.asyncconvertor.utils.Pdf2HtmlHandler;
import com.facishare.document.preview.common.dao.ConvertTaskDao;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertorMessage;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liuq on 2017/3/9.
 */

@Slf4j
@Component
public class DocConvertorProcessor {
    @Autowired
    Pdf2HtmlHandler pdf2HtmlHandler;
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    ConvertTaskDao convertTaskDao;
    private AutoConfRocketMQProcessor autoConfRocketMQProcessor;
    private static final String KEY_NAME_SERVER = "NAMESERVER";
    private static final String KEY_GROUP = "GROUP_PROVIDER";
    private static final String KEY_TOPICS = "TOPICS";
    public void init() {
        log.info("begin consumer queue!");
        autoConfRocketMQProcessor = new AutoConfRocketMQProcessor("fs-dps-mq", KEY_NAME_SERVER, KEY_GROUP, KEY_TOPICS, (MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            list.forEach((MessageExt messageExt) -> {
                ConvertorMessage convertorMessage = ConvertorMessage.builder().build();
                convertorMessage.fromProto(messageExt.getBody());
                try {
                    doConvert(convertorMessage);
                } catch (Exception ex) {
                    log.error("do convert happened exception,params:{}",ex, JSON.toJSONString(convertorMessage));
                }
            });
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        autoConfRocketMQProcessor.init();
    }

    private void doConvert(ConvertorMessage convertorMessage) throws InterruptedException {
        log.info("begin do convert,params:{}", JSON.toJSONString(convertorMessage));
        String ea = convertorMessage.getEa();
        String path = convertorMessage.getNpath();
        int page = convertorMessage.getPage();
        int status = convertTaskDao.getTaskStatus(ea, path, page);
        if (status == 0) {
            convertTaskDao.beginExcute(ea, path, page);
            String filePath = convertorMessage.getFilePath();
            String dataFilePath = pdf2HtmlHandler.doConvert(page, filePath);
            if (!Strings.isNullOrEmpty(dataFilePath)) {
                previewInfoDao.savePreviewInfo(ea, path, dataFilePath);
                convertTaskDao.endExcute(ea, path, page);
            }
        }
    }
}
