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
import com.fxiaoke.metrics.CounterService;
import com.github.autoconf.ConfigFactory;
import com.github.autoconf.api.IConfig;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
    @Autowired
    private CounterService counterService;
    private AutoConfRocketMQProcessor autoConfRocketMQProcessor;
    private static final String KEY_NAME_SERVER = "NAMESERVER";
    private static final String KEY_GROUP = "GROUP_CONSUMER";
    private static final String KEY_TOPICS = "TOPICS";
    private static String configName = "";


    public void init() throws UnknownHostException {
        InetAddress ia = InetAddress.getLocalHost();
        String host = ia.getHostName();
        log.info("host:{},begin consumer queue!", host);
        ConfigFactory.getInstance().getConfig("fs-dps-config", config -> loadConfig(config, host));
        autoConfRocketMQProcessor = new AutoConfRocketMQProcessor(configName, KEY_NAME_SERVER, KEY_GROUP, KEY_TOPICS, (MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            list.forEach((MessageExt messageExt) -> {
                ConvertorMessage convertorMessage = ConvertorMessage.builder().build();
                convertorMessage.fromProto(messageExt.getBody());
                try {
                    doConvert(convertorMessage);
                } catch (Exception ex) {
                    log.error("do convert happened exception,params:{}", ex, JSON.toJSONString(convertorMessage));
                }
            });
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        autoConfRocketMQProcessor.init();
    }

    public void loadConfig(IConfig config, String host) {
        configName = config.get(host);
    }

    private void doConvert(ConvertorMessage convertorMessage) throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
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
                counterService.inc("convert-pdf2html-ok");
                previewInfoDao.savePreviewInfo(ea, path, dataFilePath);
                convertTaskDao.excuteSuccess(ea, path, page);
            } else {
                counterService.inc("convert-pdf2html-fail");
                convertTaskDao.excuteFail(ea, path, page);
            }
        }
        stopWatch.stop();
        log.info("end do convert,params:{},cost:{}", JSON.toJSONString(convertorMessage), stopWatch.getTime());
    }


}
