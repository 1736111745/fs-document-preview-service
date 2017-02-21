package com.facishare.document.preview.cgi.utils;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.fsi.proxy.model.warehouse.a.ADownloadFile;
import com.facishare.fsi.proxy.model.warehouse.a.User;
import com.facishare.fsi.proxy.model.warehouse.g.GFileDownload;
import com.facishare.fsi.proxy.model.warehouse.n.fileupload.NDownloadFile;
import com.facishare.fsi.proxy.service.AFileStorageService;
import com.facishare.fsi.proxy.service.GFileStorageService;
import com.facishare.fsi.proxy.service.NFileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuq on 16/8/15.
 */
@Slf4j
public class FileStorageProxy {

    @Autowired
    AFileStorageService aFileStorageService;
    @Autowired
    NFileStorageService nFileStorageService;
    @Autowired
    GFileStorageService gFileStorageService;

    public byte[] GetBytesByPath(String path, EmployeeInfo employeeInfo, String securityGroup) {
        try {
            if (path.startsWith("G_")) {
                GFileDownload.Arg arg = new GFileDownload.Arg();
                arg.downloadUser = employeeInfo.getSourceUser();
                arg.downloadSecurityGroup = securityGroup;
                arg.gPath = path;
                return gFileStorageService.downloadFile(arg).data;
            } else if (path.startsWith("A_")) {
                ADownloadFile.Arg arg = new ADownloadFile.Arg();
                arg.setaPath(path);
                arg.setBusiness("Preview");
                arg.setFileSecurityGroup(securityGroup);
                User user=new User(employeeInfo.getEmployeeAccount(),employeeInfo.getEmployeeId());
                arg.setUser(user);
                log.info("download a file,arg:{}", JSON.toJSONString(arg));
                return aFileStorageService.downloadFile(arg).getData();
            } else {
                NDownloadFile.Arg arg = new NDownloadFile.Arg();
                arg.setnPath(path);
                arg.setDownloadSecurityGroup(securityGroup);
                arg.setDownloadUser(employeeInfo.getSourceUser());
                arg.setEa(employeeInfo.getEa());
                return nFileStorageService.nDownloadFile(arg, employeeInfo.getEa()).getData();
            }
        } catch (Exception e) {
            log.error("downloadFile:ea:{},sourceUser:{},path:{},securityGroup:{}", employeeInfo.getEa(), employeeInfo.getSourceUser(), path, securityGroup, e);
            return null;
        }
    }

    public void DownloadAndSave(String path, EmployeeInfo employeeInfo, String securityGroup, String originalFilePath) throws IOException {
        File originalFile = new File(originalFilePath);
        if (!originalFile.exists()) {
            byte[] bytes = GetBytesByPath(path, employeeInfo, securityGroup);
            if (bytes != null && bytes.length > 0) {
                FileUtils.writeByteArrayToFile(originalFile, bytes);
            }
        }
    }
}
