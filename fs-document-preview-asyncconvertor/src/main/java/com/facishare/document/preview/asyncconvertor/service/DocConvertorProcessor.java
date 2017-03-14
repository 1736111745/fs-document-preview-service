package com.facishare.document.preview.asyncconvertor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.facishare.document.preview.common.model.ConvertorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liuq on 2017/3/9.
 */
@Slf4j
@Component
public class DocConvertorProcessor implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
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
    }

    private void doConvert(ConvertorMessage convertorMessage) {
        log.info("begin do convert,params:{}", JSON.toJSONString(convertorMessage));
        String npath = convertorMessage.getNpath();
        int page = convertorMessage.getPage();

    }
}
