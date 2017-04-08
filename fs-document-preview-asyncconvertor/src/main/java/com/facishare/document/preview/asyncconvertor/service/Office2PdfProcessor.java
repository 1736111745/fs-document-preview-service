package com.facishare.document.preview.asyncconvertor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.facishare.common.rocketmq.AutoConfRocketMQProcessor;
import com.facishare.document.preview.asyncconvertor.utils.OnlineOfficeServerUtil;
import com.facishare.document.preview.common.dao.ConvertOffice2PdfTaskDao;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertOffice2PdfMessage;
import com.facishare.document.preview.common.utils.SampleUUID;
import com.fxiaoke.metrics.CounterService;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by liuq on 2017/3/9.
 */

@Slf4j
@Component
public class Office2PdfProcessor {
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    ConvertOffice2PdfTaskDao convertTaskDao;
    @Autowired
    private CounterService counterService;
    @Autowired
    OnlineOfficeServerUtil onlineOfficeServerUtil;
    private AutoConfRocketMQProcessor autoConfRocketMQProcessor;
    private static final String KEY_NAME_SERVER = "NAMESERVER";
    private static final String KEY_GROUP = "GROUP_CONSUMER";
    private static final String KEY_TOPICS = "TOPICS";

    //todo:office2pdf转换器，cgi的js轮询

    public void init() {
        log.info("begin consumer office2pdf queue!");
        autoConfRocketMQProcessor = new AutoConfRocketMQProcessor("fs-dps-mq-office2pdf", KEY_NAME_SERVER, KEY_GROUP, KEY_TOPICS, (MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            list.forEach((MessageExt messageExt) -> {
                ConvertOffice2PdfMessage convertorMessage = ConvertOffice2PdfMessage.builder().build();
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

    private void doConvert(ConvertOffice2PdfMessage convertorMessage) throws InterruptedException, IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("begin do convert,params:{}", JSON.toJSONString(convertorMessage));
        String ea = convertorMessage.getEa();
        String path = convertorMessage.getPath();
        int employeeId = convertorMessage.getEmployeeId();
        String sg = convertorMessage.getSg();
        int status = convertTaskDao.getTaskStatus(ea, path);
        if (status == 0) {
            convertTaskDao.beginExecute(ea, path);
            String ext = FilenameUtils.getExtension(path);
            String dataFilePath = ext.contains("doc") ? onlineOfficeServerUtil.ConvertWord2Pdf(ea, employeeId, path, sg)
                    : onlineOfficeServerUtil.ConvertPPT2Pdf(ea, employeeId, path, sg);
            String indexName = ext.contains("doc") ? "convert-word2pdf" : "convert-ppt2pdf";
            if (!Strings.isNullOrEmpty(dataFilePath)) {
                counterService.inc(indexName + "--ok");
                previewInfoDao.savePreviewInfo(ea, path, dataFilePath);
                convertTaskDao.executeSuccess(ea, path);
            } else {
                counterService.inc(indexName + "--fail");
                convertTaskDao.executeFail(ea, path);
            }
        }
        stopWatch.stop();
        log.info("end do convert,params:{},cost:{}", JSON.toJSONString(convertorMessage), stopWatch.getTime());
    }
}
