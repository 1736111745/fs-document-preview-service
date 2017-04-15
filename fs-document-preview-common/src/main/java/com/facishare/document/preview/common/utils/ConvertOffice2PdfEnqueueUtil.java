package com.facishare.document.preview.common.utils;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.dao.Office2PdfTaskDao;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertMessage;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by liuq on 2017/4/6.
 * office文件（包括pdf）转换为pdf的消息队列
 */
@Slf4j
@Component
public class ConvertOffice2PdfEnqueueUtil {
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    Office2PdfTaskDao office2PdfTaskDao;
    @Resource(name = "office2pdfProvider")
    ConvertorQueueProvider office2pdf;

    public void enqueue(String ea, String path) {
        log.info("begin enqueue,ea:{},path:{}",ea,path);
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        if(previewInfo==null) return;
        int pageCount = previewInfo.getPageCount();
        List<String> dataFilePathList = previewInfo.getFilePathList();
        if (dataFilePathList == null)
            dataFilePathList = Lists.newArrayList();
        List<Integer> hasNotConvertPageList = Lists.newArrayList();
        for (int i = 1; i < pageCount + 1; i++) {
            if (!dataFilePathList.contains(i + ".html")) {
                {
                    hasNotConvertPageList.add(i);
                }
            }
        }
        List<Integer> needEnqueuePageList = office2PdfTaskDao.batchAddTask(ea, path, hasNotConvertPageList);
        log.info("needEnqueuePageList:{}", JSON.toJSON(needEnqueuePageList));
        String originalFilePath = previewInfo.getOriginalFilePath();
        needEnqueuePageList.forEach(p -> {
            ConvertMessage convertorMessage = ConvertMessage.builder().npath(path).ea(ea).page(p).filePath(originalFilePath).build();
            office2pdf.enqueue(convertorMessage);
        });
    }
}
