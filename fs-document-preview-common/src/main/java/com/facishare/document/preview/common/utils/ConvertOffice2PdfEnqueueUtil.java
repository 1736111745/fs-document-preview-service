package com.facishare.document.preview.common.utils;

import com.facishare.document.preview.common.dao.ConvertOffice2PdfTaskDao;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertOffice2PdfMessage;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.mq.ConvertorQueueProvider;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by liuq on 2017/4/8.
 */
@Component
public class ConvertOffice2PdfEnqueueUtil {
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    ConvertOffice2PdfTaskDao convertOffice2PdfTaskDao;
    @Resource(name = "office2pdfProvider")
    ConvertorQueueProvider convertOffice2Pdf;

    public void enqueue(String ea, int employeeId, String path, String sg) {
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        if (previewInfo == null) return;
        String pdfFile = previewInfo.getPdfFilePath();
        if (Strings.isNullOrEmpty(pdfFile)) {
            ConvertOffice2PdfMessage convertOffice2PdfMessage = ConvertOffice2PdfMessage.builder().ea(ea).employeeId(employeeId).path(path).sg(sg).build();
            int status = convertOffice2PdfTaskDao.getTaskStatus(ea, path);
            if (status == -1) {
                convertOffice2PdfTaskDao.addTask(ea, path);
                convertOffice2Pdf.convertOffice2Pdf(convertOffice2PdfMessage);
            }
        }
    }
}
