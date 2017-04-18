package com.facishare.document.preview.cgi.service;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.*;
import com.facishare.document.preview.common.utils.aspose.PageInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by wuzh on 2016/11/23.
 */
@Slf4j
@Service
public class PreviewService {
    @Autowired
    FileStorageProxy fileStorageProxy;
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    Office2PdfApiHelper office2PdfApiHelper;
    @Autowired
    PageInfoHelper pageInfoHelper;

    /**
     * 手机预览
     *
     * @param employeeInfo
     * @param npath
     * @param securityGroup
     * @return
     * @throws Exception
     */
    public PreviewInfoEx getPreviewInfo(EmployeeInfo employeeInfo, String npath, String securityGroup) throws Exception {
        String ea = employeeInfo.getEa();
        int employeeId = employeeInfo.getEmployeeId();
        PreviewInfoEx previewInfoEx = new PreviewInfoEx();
        try {
            PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, npath);
            int pageCount;
            List<String> sheetNames;
            if (previewInfo == null) {
                String extension = FilenameUtils.getExtension(npath).toLowerCase();
                byte[] bytes = fileStorageProxy.GetBytesByPath(npath, ea, employeeId, securityGroup);
                if (bytes != null && bytes.length > 0) {
                    if (bytes.length > 1024 * 1024 * 30) {
                        previewInfoEx.setSuccess(false);
                        previewInfoEx.setPreviewInfo(null);
                        previewInfoEx.setErrorMsg("当前文件大于30M，不支持手机预览！");
                    } else {
                        String dataDir = new PathHelper(ea).getDataDir();
                        String fileName = SampleUUID.getUUID() + "." + extension;
                        String filePath = FilenameUtils.concat(dataDir, fileName);
                        FileUtils.writeByteArrayToFile(new File(filePath), bytes);
                        //首先检测文档是否加密
                        PageInfo pageInfo = pageInfoHelper.getPageInfo(npath, filePath);
                        if (pageInfo.isSuccess()) {
                            pageCount = pageInfo.getPageCount();
                            sheetNames = pageInfo.getSheetNames();
                            previewInfo = previewInfoDao.initPreviewInfo(ea, employeeId, npath, filePath, dataDir, bytes.length, pageCount, sheetNames);
                            previewInfoEx.setSuccess(true);
                            previewInfoEx.setPreviewInfo(previewInfo);
                        } else {
                            previewInfoEx.setSuccess(false);
                            previewInfoEx.setPreviewInfo(null);
                            previewInfoEx.setErrorMsg(pageInfo.getErrorMsg());
                        }
                    }
                } else {
                    previewInfoEx.setSuccess(false);
                    previewInfoEx.setPreviewInfo(null);
                    previewInfoEx.setErrorMsg("该文档无法预览!");
                    log.warn("npath:{} can't been download!", npath);
                }
            } else {
                previewInfoEx.setSuccess(true);
                previewInfoEx.setPreviewInfo(previewInfo);
            }
        } catch (Exception e) {
            log.error("getPreviewInfo happened exception!,npath:{}", npath, e);
        }
        return previewInfoEx;
    }

    private PageInfo getPageInfoWithOos(String path, String filePath) throws IOException {
        int pageCount = office2PdfApiHelper.getPageCount(path, filePath);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setErrorMsg("");
        pageInfo.setPageCount(pageCount);
        pageInfo.setSuccess(pageCount > 0);
        return pageInfo;
    }
}
