package com.facishare.document.preview.common.utils;

import com.facishare.document.preview.common.dao.Office2PdfTaskDao;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertMessageBase;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
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
    ConvertorQueueProvider convertorQueueProvider;

    public void enqueue(String ea, String path) {
        log.info("begin enqueue,ea:{},path:{}", ea, path);
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        if (previewInfo == null) return;
        List<String> dataFilePathList = previewInfo.getFilePathList();
        if (dataFilePathList.size() == previewInfo.getPageCount()) return;
        int status = office2PdfTaskDao.getTaskStatus(ea, path);
        if (status == -1) {
            office2PdfTaskDao.addTask(ea, path);
            ConvertMessageBase convertorMessage = new ConvertMessageBase();
            convertorMessage.setEa(ea);
            convertorMessage.setFilePath(previewInfo.getOriginalFilePath());
            convertorMessage.setNpath(path);
            convertorQueueProvider.office2pdf(convertorMessage);
        }
    }
}
