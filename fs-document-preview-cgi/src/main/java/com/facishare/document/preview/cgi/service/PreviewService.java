package com.facishare.document.preview.cgi.service;

import com.facishare.document.preview.api.model.arg.GetPageCountArg;
import com.facishare.document.preview.api.service.DocConvertService;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.cgi.utils.OnlineOfficeServerUtil;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.DocPageInfoHelper;
import com.facishare.document.preview.common.utils.OfficeFileEncryptChecker;
import com.facishare.document.preview.common.utils.PathHelper;
import com.facishare.document.preview.common.utils.SampleUUID;
import com.fxiaoke.common.http.handler.SyncCallback;
import com.fxiaoke.common.http.spring.OkHttpSupport;
import com.fxiaoke.release.FsGrayRelease;
import com.fxiaoke.release.FsGrayReleaseBiz;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    DocConvertService docConvertService;
    @Autowired
    OnlineOfficeServerUtil onlineOfficeServerUtil;
    private FsGrayReleaseBiz gray = FsGrayRelease.getInstance("dps");

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
                String grayConfig = "office2pdf";
                String user = "E." + employeeInfo.getEa() + "." + employeeInfo.getEmployeeId();
                boolean office2pdf = gray.isAllow(grayConfig, user);
                String extension = FilenameUtils.getExtension(path).toLowerCase();
                byte[] bytes;
                if (extension.contains("xls") || extension.contains("pdf")) {
                    bytes = fileStorageProxy.GetBytesByPath(path, ea,employeeId, securityGroup);
                } else {
                    bytes = !office2pdf ? fileStorageProxy.GetBytesByPath(path, ea,employeeId, securityGroup)
                            : onlineOfficeServerUtil.downloadPdfFile(ea, employeeId, path, securityGroup);
                }

                if (bytes != null && bytes.length > 0) {
                    extension = office2pdf ? (extension.contains("xls") ? extension : "pdf") : extension;
                    String dataDir = new PathHelper(ea).getDataDir();
                    String fileName = SampleUUID.getUUID() + "." + extension;
                    String filePath = FilenameUtils.concat(dataDir, fileName);
                    //下载下来保存便于文档转换方便 // TODO: 2016/11/10 当所有的页码都转码完毕后需要删除.
                    FileUtils.writeByteArrayToFile(new File(filePath), bytes);
                    //首先检测文档是否加密
                    boolean isEncrypt = OfficeFileEncryptChecker.check(filePath);
                    PageInfo pageInfo;
                    if (!isEncrypt) {
                        if (extension.equals("docx") || extension.equals("doc") || extension.equals("ppt")) {
                            pageInfo = getPageInfoWithYozo(filePath);
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

    private PageInfo getPageInfoWithYozo(String filePath) throws Exception {
        GetPageCountArg arg = GetPageCountArg.builder().filePath(filePath).build();
        int pageCount = docConvertService.getPageCount(arg).getPageCount();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setErrorMsg("");
        pageInfo.setPageCount(pageCount);
        pageInfo.setSuccess(pageCount > 0);
        return pageInfo;
    }
}
