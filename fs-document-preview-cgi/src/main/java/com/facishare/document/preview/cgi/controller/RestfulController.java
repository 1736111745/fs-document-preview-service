package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewInfoEx;
import com.facishare.document.preview.cgi.service.PreviewService;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sun.net.www.http.HttpClient;

import java.io.File;

@RestController
@RequestMapping("/restful")
@Slf4j
public class RestfulController {
    @Autowired
    private PreviewService previewService;
    @Autowired
    PreviewInfoDao previewInfoDao;
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
            Preconditions.checkNotNull(ea, "ea is null");
            Preconditions.checkNotNull(employeeId, "employeeId is null");
            PreviewInfoEx previewInfoEx = getPreviewInfo(createEmployeeInfo(ea, employeeId), filePath);
            Preconditions.checkNotNull(previewInfoEx.getPreviewInfo(), "document can't found!");
            ret = String.format("{\"pageCount\":%d}", previewInfoEx.getPreviewInfo().getPageCount());
        } catch (Exception e) {
            log.error("/document/getPageCount |filePath: {} | ea: {} | ei: {} ", filePath, ea, employeeId, e);
            ret = String.format("{\"error\":\"%s\"}", e.getMessage());
        }
        return ret;
    }

    /**
     *
     * @param filePath 文件路径
     * @param ea  企业账号
     * @return
     */
    @RequestMapping(value = "/maintance/preview/clean", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public boolean documentPageClean(String filePath, String ea) {
       //todo:1.删除monogo记录 2.清理文件。

        //从mongo中读取逻辑路径（记录）
        PreviewInfo previewInfo=previewInfoDao.getInfoByPath(ea,filePath);
        if (previewInfo == null) {
            //previewInfoDao.clean(ea , filePath);
            return true;
        }
        //获取文件夹实际路径
        String fileDir=previewInfo.getDataDir();
        File file = new File(fileDir);
        //递归删除，预防文件夹中还存在文件夹
        FileUtils.deleteQuietly(file);
        //删除mongo数据
        previewInfoDao.clean(ea , filePath);
        log.info("删除成功");
        return true;

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
