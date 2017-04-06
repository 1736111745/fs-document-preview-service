package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.model.PreviewWayEntity;
import com.facishare.document.preview.cgi.utils.UrlParametersHelper;
import com.fxiaoke.release.FsGrayRelease;
import com.fxiaoke.release.FsGrayReleaseBiz;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by liuq on 16/9/29.
 */
@Slf4j
@Controller
@RequestMapping("/")
public class PreviewConfigController {
    private FsGrayReleaseBiz gray = FsGrayRelease.getInstance("dps");
    @ReloadableProperty("allowPreviewExtension")
    private String allowPreviewExtension = "doc|docx|xls|xlsx|ppt|pptx|pdf";

    @ResponseBody
    @RequestMapping(value = "/preview/getPreviewConfig", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getPreviewWay(HttpServletRequest request) {
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String client = UrlParametersHelper.safeGetRequestParameter(request, "client");
        boolean isIOS = client.toLowerCase().equals("ios");
        String grayConfig = isIOS ? "newway_iOS" : "newway_android";
        PreviewWayEntity entity = new PreviewWayEntity();
        String user = "E." + employeeInfo.getEa() + "." + employeeInfo.getEmployeeId();
        boolean newway = gray.isAllow(grayConfig, user);
        String name = UrlParametersHelper.safeGetRequestParameter(request, "name");
        boolean useNewWay = true;
        if (!Strings.isNullOrEmpty(name)) {
            String extension = FilenameUtils.getExtension(name).toLowerCase();
            if (allowPreviewExtension.indexOf(extension) == -1) {
                useNewWay = false;
            }
        }
        if (newway && useNewWay) {
            entity.setWay(1);
            String byTokenUrl = "/dps/preview/bytoken?token={0}&name={1}";
            String byPathUrl = "/dps/preview/bypath?path={0}&name={1}";
            entity.setPreviewByPathUrlFormat(byPathUrl);
            entity.setPreviewByTokenUrlFormat(byTokenUrl);
        } else
            entity.setWay(0);
        String json = JSON.toJSONString(entity);
        return json;
    }
}
