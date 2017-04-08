package com.facishare.document.preview.common.utils;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.dao.ConvertPdf2HtmlTaskDao;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import java.util.List;

/**
 * Created by liuq on 2017/4/6.
 */
@Slf4j
@Component
public class ConvertPdf2HtmlEnqueueUtil {
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    ConvertPdf2HtmlTaskDao convertPdf2HtmlTaskDao;

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
        log.info("hasNotConvertPageList:{}", JSON.toJSON(hasNotConvertPageList));
        List<Integer> needEnqueuePageList = convertPdf2HtmlTaskDao.batchAddTask(ea, path, hasNotConvertPageList);
        log.info("needEnqueuePageList:{}", JSON.toJSON(needEnqueuePageList));
        String originalFilePath = previewInfo.getOriginalFilePath();
        String pdfFilePath = previewInfo.getPdfFilePath();
        String finalFilePath = !Strings.isNullOrEmpty(pdfFilePath) ? pdfFilePath : originalFilePath;
        needEnqueuePageList.forEach(p -> {
            ConvertPdf2HtmlMessage convertorMessage = ConvertPdf2HtmlMessage.builder().npath(path).ea(ea).page(p).filePath(finalFilePath).build();
            ConvertorQueueProvider.getInstance().convertPdf2Html(convertorMessage);
        });
    }
}
