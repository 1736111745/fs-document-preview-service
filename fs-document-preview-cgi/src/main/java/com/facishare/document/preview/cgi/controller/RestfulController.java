package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.service.PreviewService;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.DocPageInfoHelper;
import com.facishare.document.preview.common.utils.PathHelper;
import com.facishare.document.preview.common.utils.SampleUUID;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.aspectj.weaver.Dump;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restful")
@Slf4j
public class RestfulController {
    @Autowired
    private PreviewInfoDao previewInfoDao;
    @Autowired
    FileStorageProxy fileStorageProxy;

    /**
     * 获取文档页码数
     *
     * @param filePath   文件路径
     * @param ea         企业账号
     * @param employeeId 员工id
     * @return
     */
    @RequestMapping(value = "/document/getPageCount", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String documentPageCount(String filePath, String ea, Integer employeeId) {
        String ret;
        int pageCount = 0;
        try {
            PreviewInfo info = previewInfoDao.getInfoByPath(ea, filePath);
            if (info != null) {
                pageCount = info.getPageCount();
            } else {
                String dirTempPath = new PathHelper().getConvertTempPath();
                String ext = FilenameUtils.getExtension(filePath);
                String tempFilePath = FilenameUtils.concat(dirTempPath, SampleUUID.getUUID() + "." + ext);
                fileStorageProxy.DownloadAndSave(filePath, ea, employeeId, "", tempFilePath);
                pageCount = DocPageInfoHelper.getPageInfo(filePath).getPageCount();
            }
            ret = String.format("{\"pageCount\":%d}", pageCount);
        } catch (Exception e) {
            log.error("/document/getPageCount |filePath: {} | ea: {} | ei: {} ", filePath, ea, employeeId, e);
            ret = String.format("{\"error\":\"%s\"}", e.getMessage());
        }
        return ret;
    }
}
