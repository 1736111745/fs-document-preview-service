package com.facishare.document.preview.cgi.service;

import com.facishare.document.preview.api.model.arg.GetPageCountArg;
import com.facishare.document.preview.api.service.DocConvertService;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.*;
import com.fxiaoke.release.FsGrayRelease;
import com.fxiaoke.release.FsGrayReleaseBiz;
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

    /**
     * 手机预览
     *
     * @param employeeInfo
     * @param path
     * @param securityGroup
     * @return
     * @throws Exception
     */
    public PreviewInfoEx getPreviewInfo(EmployeeInfo employeeInfo, String path, String securityGroup) throws Exception {
        String ea = employeeInfo.getEa();
        int employeeId = employeeInfo.getEmployeeId();
        PreviewInfoEx previewInfoEx = new PreviewInfoEx();
        try {
            PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
            int pageCount;
            List<String> sheetNames;
            if (previewInfo == null) {
                String extension = FilenameUtils.getExtension(path).toLowerCase();
                byte[] bytes = fileStorageProxy.GetBytesByPath(path, ea, employeeId, securityGroup);
                if (bytes != null && bytes.length > 0) {
                    String dataDir = new PathHelper(ea).getDataDir();
                    String fileName = SampleUUID.getUUID() + "." + extension;
                    String filePath = FilenameUtils.concat(dataDir, fileName);
                    FileUtils.writeByteArrayToFile(new File(filePath), bytes);
                    //首先检测文档是否加密
                    boolean isEncrypt = OfficeFileEncryptChecker.check(filePath);
                    PageInfo pageInfo;
                    if (!isEncrypt) {
                        if (extension.contains("ppt") || extension.contains("doc") || extension.contains("pdf")) {
                            pageInfo = getPageInfoWithOos(path,filePath);
                        } else {
                            pageInfo = DocPageInfoHelper.getPageInfo(filePath);
                        }
                        if (pageInfo.isSuccess()) {
                            pageCount = pageInfo.getPageCount();
                            sheetNames = pageInfo.getSheetNames();
                            previewInfo = previewInfoDao.initPreviewInfo(ea, employeeId, path, filePath, dataDir, bytes.length, pageCount, sheetNames);
                            previewInfoEx.setSuccess(true);
                            previewInfoEx.setPreviewInfo(previewInfo);
                        } else {
                            previewInfoEx.setSuccess(false);
                            previewInfoEx.setPreviewInfo(null);
                            previewInfoEx.setErrorMsg(pageInfo.getErrorMsg());
                        }
                    } else {
                        previewInfoEx.setSuccess(false);
                        previewInfoEx.setPreviewInfo(null);
                        previewInfoEx.setErrorMsg("该文档是为加密文档，暂不支持预览！");
                    }
                } else {
                    log.warn("path:{} can't been download!", path);
                }
            } else {
                previewInfoEx.setSuccess(true);
                previewInfoEx.setPreviewInfo(previewInfo);
            }
        } catch (Exception e) {
            log.error("getPreviewInfo happened exception!,path:{}", path, e);
        }
        return previewInfoEx;
    }

    private PageInfo getPageInfoWithOos(String path,String filePath) throws IOException {
        int pageCount = office2PdfApiHelper.getPageCount(path,filePath);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setErrorMsg("");
        pageInfo.setPageCount(pageCount);
        pageInfo.setSuccess(pageCount > 0);
        return pageInfo;
    }
}
