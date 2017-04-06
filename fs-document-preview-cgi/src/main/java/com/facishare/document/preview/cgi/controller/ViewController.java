package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.fxiaoke.release.FsGrayRelease;
import com.fxiaoke.release.FsGrayReleaseBiz;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liuq on 16/9/29.
 */
@Controller
@RequestMapping("/")
public class ViewController {
    private FsGrayReleaseBiz gray = FsGrayRelease.getInstance("dps");

    @RequestMapping(value = "/preview/bypath", method = RequestMethod.GET)
    public String previewByPath() {
        return "preview";
    }

    @RequestMapping(value = "/preview/bytoken", method = RequestMethod.GET)
    public String previewByToken() {
        return "preview";
    }

    @RequestMapping(value = "/preview/handleExcel", method = RequestMethod.GET)
    public String handleExcel() {
        return "excel2html";
    }

    @RequestMapping(value = "/preview/handlePdf", method = RequestMethod.GET)
    public String handlePdf(HttpServletRequest request) {
        EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
        String grayConfig = "pdf2html";
        String user = "E." + employeeInfo.getEa() + "." + employeeInfo.getEmployeeId();
        boolean pd2html = gray.isAllow(grayConfig, user);
        return pd2html ? "pdf2html" : "pdf2png";
    }

    @RequestMapping(value = "/preview/handleOffice2Pdf", method = RequestMethod.GET)
    public String handleOffice2Pdf() {
        return "office2pdf";
    }

    @RequestMapping(value = "/preview/handleWordAndPPT", method = RequestMethod.GET)
    public String handleWordAndPPT() {
        return "office2svg";
    }

}
