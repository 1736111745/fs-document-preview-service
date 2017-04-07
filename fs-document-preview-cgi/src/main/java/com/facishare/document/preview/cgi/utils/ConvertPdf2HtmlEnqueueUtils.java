package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.common.dao.ConvertTaskDao;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liuq on 2017/4/6.
 */
@Component
public class ConvertPdf2HtmlEnqueueUtils {
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    ConvertTaskDao convertTaskDao;
    @Autowired
    ConvertorQueueProvider convertorQueueProvider;
    public  void enqueue(String ea,String path) {
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
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
        String originalFilePath = previewInfo.getOriginalFilePath();
        String pdfFilePath = previewInfo.getPdfFilePath();
        String finalFilePath = !Strings.isNullOrEmpty(pdfFilePath) ? pdfFilePath : originalFilePath;
        List<Integer> needEnqueuePageList = convertTaskDao.batchAddTask(ea, path, hasNotConvertPageList);
        needEnqueuePageList.forEach(p -> {
            ConvertPdf2HtmlMessage convertorMessage = ConvertPdf2HtmlMessage.builder().npath(path).ea(ea).page(p).filePath(finalFilePath).build();
            convertorQueueProvider.convertPdf2Html(convertorMessage);
        });
    }
}
