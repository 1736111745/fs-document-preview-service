package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.utils.ConvertPdf2HtmlEnqueueUtils;
import com.facishare.document.preview.cgi.utils.OnlineOfficeServerUtil;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.ConvertorMessage;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.SampleUUID;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facishare.document.preview.cgi.utils.UrlParametersHelper.safeGetRequestParameter;

/**
 * Created by liuq on 2017/4/6.
 */
@Slf4j
@Controller
@RequestMapping("/")
public class Office2PdfController {
    @Autowired
    OnlineOfficeServerUtil onlineOfficeServerUtil;
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    ConvertPdf2HtmlEnqueueUtils convertPdf2HtmlEnqueueUtils;

    @ResponseBody
    @RequestMapping(value = "/preview/checkPPT2Pdf", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String checkPPT2Pdf(HttpServletRequest request) throws Exception {
        String path = safeGetRequestParameter(request, "path");
        String sg = safeGetRequestParameter(request, "sg");
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String ea = employeeInfo.getEa();
        int employeeId = employeeInfo.getEmployeeId();
        String ext = FilenameUtils.getExtension(path).toLowerCase();
        String name = SampleUUID.getUUID() + "." + ext;
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        boolean finished = false;
        if (!Strings.isNullOrEmpty(previewInfo.getPdfFilePath())) {
            finished = true;
        } else {
            String json = onlineOfficeServerUtil.checkPPT2Pdf(ea, employeeId, path, sg, name);
            JSONObject jsonObject = JSONObject.parseObject(json);
            if (jsonObject.get("Error") == null) {
                String printUrl = ((JSONObject) jsonObject.get("Result")).getString("PrintUrl");
                byte[] bytes = onlineOfficeServerUtil.downloadPdfByPrintUrl(printUrl);
                finished = true;
                savePdfFile(ea, path, bytes);
            }
        }
        Map<String, Boolean> map = new HashMap<>();
        map.put("finished", finished);
        return JSON.toJSONString(map);
    }

    @ResponseBody
    @RequestMapping(value = "/preview/checkWord2Pdf", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String checkWord2Pdf(HttpServletRequest request) throws Exception {
        String path = safeGetRequestParameter(request, "path");
        String sg = safeGetRequestParameter(request, "sg");
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String ea = employeeInfo.getEa();
        int employeeId = employeeInfo.getEmployeeId();
        String ext = FilenameUtils.getExtension(path).toLowerCase();
        String name = SampleUUID.getUUID() + "." + ext;
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        boolean finished;
        if (!Strings.isNullOrEmpty(previewInfo.getPdfFilePath())) {
            finished = true;
        } else {
            OnlineOfficeServerUtil.WordConvertInfo wordConvertInfo = onlineOfficeServerUtil.checkWord2Pdf(ea, employeeId, path, sg, name);
            finished = wordConvertInfo.isFinished();
            if (finished) {
                savePdfFile(ea, path, wordConvertInfo.getBytes());
            }
        }
        Map<String, Boolean> map = new HashMap<>();
        map.put("finished", finished);
        return JSON.toJSONString(map);
    }

    private void savePdfFile(String ea, String path, byte[] bytes) throws IOException {
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        String dataDir = previewInfo.getDataDir();
        String fileName = SampleUUID.getUUID() + ".pdf";
        String filePath = FilenameUtils.concat(dataDir, fileName);
        FileUtils.writeByteArrayToFile(new File(filePath), bytes);
        previewInfoDao.savePdfFile(ea, path, filePath);
        convertPdf2HtmlEnqueueUtils.enqueue(ea, path);
    }


}
