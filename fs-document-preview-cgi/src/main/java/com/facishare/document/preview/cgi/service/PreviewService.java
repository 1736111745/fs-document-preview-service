package com.facishare.document.preview.cgi.service;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.OfficeApiHelper;
import com.facishare.document.preview.common.utils.PathHelper;
import com.facishare.document.preview.common.utils.SampleUUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
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
    OfficeApiHelper officeApiHelper;

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
                    if (bytes.length > 1024 * 1024 * 100) {
                        previewInfoEx.setSuccess(false);
                        previewInfoEx.setPreviewInfo(null);
                        previewInfoEx.setErrorMsg("当前文件大于100M，不支持手机预览！");
                    } else {
                        String dataDir = new PathHelper(ea).getDataDir();
                        String fileName = SampleUUID.getUUID() + "." + extension;
                        String filePath = FilenameUtils.concat(dataDir, fileName);
                        FileUtils.writeByteArrayToFile(new File(filePath), bytes);
                        //旧版本office格式e转换为新版本office格式
                        if (extension.contains("xls") || extension.contains("doc") || extension.contains("ppt")) {
                            filePath = officeApiHelper.convertFile(filePath);
                        }
                        PageInfo pageInfo = officeApiHelper.getPageInfo(npath, filePath);
                        if (pageInfo.getPageCount() > 500) {
                            previewInfoEx.setSuccess(false);
                            previewInfoEx.setPreviewInfo(null);
                            previewInfoEx.setErrorMsg("当前文件页码数超过500页，不支持手机预览！");
                        } else {
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
}
