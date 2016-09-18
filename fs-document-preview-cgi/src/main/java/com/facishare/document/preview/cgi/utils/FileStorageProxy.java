package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.fsi.proxy.model.warehouse.a.ADownloadFile;
import com.facishare.fsi.proxy.model.warehouse.a.User;
import com.facishare.fsi.proxy.model.warehouse.g.GFileDownload;
import com.facishare.fsi.proxy.model.warehouse.n.fileupload.NDownloadFile;
import com.facishare.fsi.proxy.service.AFileStorageService;
import com.facishare.fsi.proxy.service.GFileStorageService;
import com.facishare.fsi.proxy.service.NFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liuq on 16/8/15.
 */
public class FileStorageProxy {

    public static final Logger LOG = LoggerFactory.getLogger(FileStorageProxy.class);
    @Autowired
    AFileStorageService aFileStorageService;
    @Autowired
    NFileStorageService nFileStorageService;
    @Autowired
    GFileStorageService gFileStorageService;

    public byte[] GetBytesByPath(String path, EmployeeInfo employeeInfo,String securityGroup) {
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
                arg.setUser(new User(employeeInfo.getEmployeeAccount(), employeeInfo.getEmployeeId()));
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
            LOG.error("downloadFile:ea:{},sourceUser:{},path:{},securityGroup:{}", employeeInfo.getEa(), employeeInfo.getSourceUser(), path, securityGroup, e);
            return null;
        }
    }

}
