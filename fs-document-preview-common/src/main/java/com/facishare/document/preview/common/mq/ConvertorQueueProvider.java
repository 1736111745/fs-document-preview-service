package com.facishare.document.preview.common.mq;

import com.alibaba.rocketmq.common.message.Message;
import com.facishare.common.rocketmq.AutoConfRocketMQSender;
import com.facishare.document.preview.common.model.ConvertMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by liuq on 2017/3/9.
 */
@Slf4j
public class ConvertorQueueProvider {

    private AutoConfRocketMQSender autoConfRocketMQSender;
    private static final String KEY_NAME_SERVER = "NAMESERVER";
    private static final String KEY_GROUP = "GROUP_PROVIDER";
    private static final String KEY_TOPICS = "TOPICS";

    private String configName;

    public void init() {
        log.info("start init rocketmq!");
        autoConfRocketMQSender = new AutoConfRocketMQSender(configName, KEY_NAME_SERVER, KEY_GROUP, KEY_TOPICS);
        autoConfRocketMQSender.init();
        log.info("finish init rocketmq!");
    }

    public void enqueue(ConvertMessage message) {
        log.info("enqueue:{}", com.alibaba.fastjson.JSON.toJSON(message));
        Message messageExt = new Message();
        messageExt.setBody(message.toProto());
        autoConfRocketMQSender.send(messageExt);
        log.info("enqueue completed!message topic:{}", messageExt.getTopic());
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }


}
