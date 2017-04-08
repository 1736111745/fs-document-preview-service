package com.facishare.document.preview.common.mq;

import com.alibaba.rocketmq.common.message.Message;
import com.facishare.common.fsi.ProtoBase;
import com.facishare.common.rocketmq.AutoConfRocketMQSender;
import com.facishare.document.preview.common.model.ConvertOffice2PdfMessage;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
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

    private static class SingletonHolder {
        private static final ConvertorQueueProvider instance = new ConvertorQueueProvider();
    }

    public static ConvertorQueueProvider getInstance() {
        return SingletonHolder.instance;
    }

    private ConvertorQueueProvider() {
        log.info("start init rocketmq!");
        autoConfRocketMQSender = new AutoConfRocketMQSender("fs-dps-mq", KEY_NAME_SERVER, KEY_GROUP, KEY_TOPICS);
        autoConfRocketMQSender.init();
        log.info("finish init rocketmq!");
    }

    private <T extends ProtoBase> void enqueue(T message, String tags) {
        log.info("enqueue:{}", com.alibaba.fastjson.JSON.toJSON(message));
        Message messageExt = new Message();
        messageExt.setTags(tags);
        messageExt.setBody(message.toProto());
        autoConfRocketMQSender.send(messageExt);
        log.info("enqueue completed!");
    }

    public void convertPdf2Html(ConvertPdf2HtmlMessage message) {
        enqueue(message, "pdf2html");
    }

    public void convertOffice2Pdf(ConvertOffice2PdfMessage message) {
        enqueue(message, "office2pdf");
    }

}
