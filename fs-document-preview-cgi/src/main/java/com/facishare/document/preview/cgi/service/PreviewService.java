package com.facishare.document.preview.cgi.service;

import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PageInfo;
import com.facishare.document.preview.cgi.model.PreviewInfo;
import com.facishare.document.preview.cgi.utils.DocPageInfoHelper;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.cgi.utils.PathHelper;
import com.facishare.document.preview.cgi.utils.SampleUUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Created by wuzh on 2016/11/23.
 */
@Service
public class PreviewService {
    @Autowired
    FileStorageProxy fileStorageProxy;
    @Autowired
    PreviewInfoDao previewInfoDao;

    /**
     * 手机预览
     *
     * @param employeeInfo
     * @param path
     * @param securityGroup
     * @return
     * @throws Exception
     */
    public PreviewInfo getPreviewInfo(EmployeeInfo employeeInfo, String path, String securityGroup) throws Exception {
        String ea = employeeInfo.getEa();
        int employeeId = employeeInfo.getEmployeeId();
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        int pageCount;
        List<String> sheetNames;
        if (previewInfo == null) {
            byte[] bytes = fileStorageProxy.GetBytesByPath(path, employeeInfo, securityGroup);
            if (bytes != null && bytes.length > 0) {
                String extension = FilenameUtils.getExtension(path).toLowerCase();
                String dataDir = new PathHelper(ea).getDataDir();
                String fileName = SampleUUID.getUUID() + "." + extension;
                String filePath = FilenameUtils.concat(dataDir, fileName);
                //下载下来保存便于文档转换方便 // TODO: 2016/11/10 当所有的页码都转码完毕后需要删除.
                FileUtils.writeByteArrayToFile(new File(filePath), bytes);
                PageInfo pageInfo = DocPageInfoHelper.GetPageInfo(bytes, filePath);
                pageCount = pageInfo.getPageCount();
                sheetNames = pageInfo.getSheetNames();
                previewInfo = previewInfoDao.initPreviewInfo(ea, employeeId, path, filePath, dataDir, bytes.length, pageCount, sheetNames);
            }
        }
        return previewInfo;
    }
}
