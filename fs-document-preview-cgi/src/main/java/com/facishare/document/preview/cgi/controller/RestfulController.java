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
import org.springframework.web.bind.annotation.*;
import sun.net.www.http.HttpClient;

import java.io.File;
import java.util.List;

import static com.facishare.document.preview.common.model.FileTokenFields.filePath;

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
     * @param filePathList 文件路径
     * @param ea  企业账号
     * @return
     * 删除预览文档
     */
    @RequestMapping(value = "/maintance/preview/clean", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public boolean documentPageClean(String ea,@RequestParam(required = false, value = "filePathList")List<String> filePathList) {
       //todo:1.删除monogo记录 2.清理文件。
        //从mongo中读取逻辑路径（记录）
       // PreviewInfo previewInfo=previewInfoDao.getInfoByPath(ea,filePath);
        List<PreviewInfo> previewInfoList = previewInfoDao.getInfoByPathList(ea,filePathList);
        if (previewInfoList == null) {
            return true;
        }
        for(PreviewInfo previewInfo:previewInfoList){
            //获取文件夹实际路径
            String fileDir=previewInfo.getDataDir();
            File file = new File(fileDir);
            //递归删除，预防文件夹中还存在文件夹
            FileUtils.deleteQuietly(file);
        }
        //批量删除mongo数据
        previewInfoDao.patchClean(ea , filePathList);
        log.info("删除成功");
        return true;
    }
    /**
     *
     * @param ea
     * @param filePathList
     * @return
     * 查询预览文档
     */
    @RequestMapping(value = "/maintance/preview/query", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public List<PreviewInfo> documentPageView(String ea,@RequestParam(required = false, value = "filePathList")List<String> filePathList){
        List<PreviewInfo> previewInfoList = previewInfoDao.getInfoByPathList(ea,filePathList);
        if (previewInfoList != null) {
            return previewInfoList;
        }else {
            return null;
        }
    }

    /**
     *
     * @param employeeInfo
     * @param path
     * @return
     * @throws Exception
     */
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
