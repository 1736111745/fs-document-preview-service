package com.facishare.document.preview.common.mq;

import com.facishare.document.preview.common.model.ConvertorMessage;
import lombok.extern.slf4j.Slf4j;
import com.facishare.common.rocketmq.AutoConfRocketMQSender;
import com.alibaba.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

/**
 * Created by liuq on 2017/3/9.
 */
@Slf4j
@Component
public class ConvertorQueueProvider {

    private AutoConfRocketMQSender autoConfRocketMQSender;
    private static final String KEY_NAME_SERVER = "NAMESERVER";
    private static final String KEY_GROUP = "GROUP_PROVIDER";
    private static final String KEY_TOPICS = "TOPICS";

    public ConvertorQueueProvider(String configName) {
        log.info("start init rocketmq!");
        autoConfRocketMQSender = new AutoConfRocketMQSender(configName, KEY_NAME_SERVER, KEY_GROUP, KEY_TOPICS);
        autoConfRocketMQSender.init();
        log.info("finish init rocketmq!");
    }

    private void enqueue(ConvertorMessage message, String tags) {
        log.info("enqueue:{}", com.alibaba.fastjson.JSON.toJSON(message));
        Message messageExt = new Message();
        messageExt.setTags(tags);
        messageExt.setBody(message.toProto());
        autoConfRocketMQSender.send(messageExt);
        log.info("enqueue completed!");
    }

    public void convertPdf(ConvertorMessage message) {
        enqueue(message,"pdf2html");
    }

}
