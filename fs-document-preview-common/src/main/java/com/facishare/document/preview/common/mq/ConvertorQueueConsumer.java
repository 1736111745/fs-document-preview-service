package com.facishare.document.preview.common.mq;

import com.alibaba.rocketmq.client.consumer.listener.MessageListener;
import com.facishare.common.rocketmq.AutoConfRocketMQProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by liuq on 2017/3/9.
 */
@Slf4j
public class ConvertorQueueConsumer {

    private AutoConfRocketMQProcessor autoConfRocketMQProcessor;
    private static final String KEY_NAME_SERVER = "NAMESERVER";
    private static final String KEY_GROUP = "GROUP_PROVIDER";
    private static final String KEY_TOPICS = "TOPICS";

    public  ConvertorQueueConsumer(String configName, MessageListener messageListener) {
        log.info("configName:{}",configName);
        autoConfRocketMQProcessor = new AutoConfRocketMQProcessor(configName, KEY_NAME_SERVER, KEY_GROUP, KEY_TOPICS, messageListener);
        autoConfRocketMQProcessor.init();
        log.info("consumer finish init!");
    }
}
