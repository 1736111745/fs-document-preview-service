package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
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
     * @param ea 企业账号
     * @param employeeId        员工id
     * @return
     */
    @RequestMapping(value = "/document/getPageCount", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String documentPageCount(String filePath, String ea, Integer employeeId) {
        String ret;
        try {
            Preconditions.checkNotNull(filePath, "filePath is null");
            Preconditions.checkNotNull(ea, "enterpriseAccount is null");
            Preconditions.checkNotNull(employeeId, "employeeId is null");
            PreviewInfoEx previewInfoEx = getPreviewInfo(createEmployeeInfo(ea, employeeId), filePath);
            Preconditions.checkNotNull(previewInfoEx.getPreviewInfo(), "document can't found!");
            ret = String.format("{\"value\":%d}", previewInfoEx.getPreviewInfo().getPageCount());
        } catch (Exception e) {
            log.error("/document/getPageCount |filePath: {} | ea: {} | ei: {} ", filePath, ea, employeeId, e);
            ret = String.format("{\"error\":\"%s\"}", e.getMessage());
        }
        return ret;
    }

    public PreviewInfoEx getPreviewInfo(EmployeeInfo employeeInfo, String path) throws Exception {
        return previewService.getPreviewInfo(employeeInfo, path,"");
    }
    private EmployeeInfo createEmployeeInfo(String ea, int ei) {
        EmployeeInfo employeeInfo = new EmployeeInfo();
        employeeInfo.setEa(ea);
        employeeInfo.setEmployeeId(ei);
        return employeeInfo;
    }
}
