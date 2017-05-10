package com.facishare.document.preview.common.mq;

import com.alibaba.rocketmq.common.message.Message;
import com.facishare.common.fsi.ProtoBase;
import com.facishare.common.rocketmq.AutoConfRocketMQSender;
import com.facishare.document.preview.common.model.ConvertMessageBase;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

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

    public void office2pdf(ConvertMessageBase message) {enqueue(message);}

    private <T extends ProtoBase> void enqueue(T message) {
        log.info("enqueue:{}", com.alibaba.fastjson.JSON.toJSON(message));
        Message messageExt = new Message();
        messageExt.setBody(message.toProto());
        autoConfRocketMQSender.send(messageExt);
        log.info("enqueue completed!message topic:{}", messageExt.getTopic());
    }

    public void pdf2html(ConvertPdf2HtmlMessage message) {
        enqueue(message);
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }


}
