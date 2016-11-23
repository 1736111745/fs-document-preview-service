package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.service.PreviewService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restful")
@Slf4j
public class RestfulController {
    @Autowired
    private PreviewService previewService;

    /**
     * 获取文档页码数
     *
     * @param filePath          文件路径
     * @param enterpriseAccount 企业账号
     * @param employeeId        员工id
     * @return
     */
    @RequestMapping(value = "/document/pageCount/{filePath}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String documentPageCount(@PathVariable String filePath, String enterpriseAccount, Integer employeeId) {
        log.debug("/document/pageCount/{} | ea: {} | ei: {} ", filePath, enterpriseAccount, employeeId);
        String ret;
        try {
            Preconditions.checkNotNull(enterpriseAccount, "enterpriseAccount is null");
            Preconditions.checkNotNull(employeeId, "employeeId is null");
            PreviewInfo previewInfo = getPreviewInfo(createEmployeeInfo(enterpriseAccount, employeeId), filePath);
            Preconditions.checkNotNull(previewInfo, "document can't found!");
            ret = String.format("{\"value\":%d}", previewInfo.getPageCount());

        } catch (Exception e) {
            log.error("/document/pageCount/{} | ea: {} | ei: {} ", filePath, enterpriseAccount, employeeId, e);
            ret = String.format("{\"error\":\"%s\"}", e.getMessage());
        }
        return ret;
    }

    /**
     * 获取文档大小
     *
     * @param filePath          文件路径
     * @param enterpriseAccount 企业账号
     * @param employeeId        员工id
     * @return
     */
    @RequestMapping(value = "/document/fileSize/{filePath}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String documentFileSize(@PathVariable String filePath, String enterpriseAccount, Integer employeeId) {
        log.debug("/document/fileSize/{} | ea: {} | ei: {} ", filePath, enterpriseAccount, employeeId);
        String ret;
        try {
            Preconditions.checkNotNull(enterpriseAccount, "enterpriseAccount is null");
            Preconditions.checkNotNull(employeeId, "employeeId is null");
            PreviewInfo previewInfo = getPreviewInfo(createEmployeeInfo(enterpriseAccount, employeeId), filePath);
            Preconditions.checkNotNull(previewInfo, "document can't found!");
            ret = String.format("{\"value\":%d}", previewInfo.getDocSize());

        } catch (Exception e) {
            log.error("/document/fileSize/{} | ea: {} | ei: {} ", filePath, enterpriseAccount, employeeId, e);
            ret = String.format("{\"error\":\"%s\"}", e.getMessage());
        }
        return ret;
    }

    public PreviewInfo getPreviewInfo(EmployeeInfo employeeInfo, String path) throws Exception {
        return previewService.getPreviewInfo(employeeInfo, path, null);
    }

    private EmployeeInfo createEmployeeInfo(String ea, int ei) {
        EmployeeInfo employeeInfo = new EmployeeInfo();
        employeeInfo.setEa(ea);
        employeeInfo.setEmployeeId(ei);
        return employeeInfo;
    }

}
