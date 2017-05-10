package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.utils.UrlParametersHelper;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liuq on 16/9/29.
 */
@Controller
@RequestMapping("/")
public class ViewController {
    @ReloadableProperty("htmlWidthList")
    private String htmlWidthList = "1000|640";

    @RequestMapping(value = "/preview/bypath", method = RequestMethod.GET)
    public String previewByPath() {
        return "preview";
    }

    @RequestMapping(value = "/preview/bytoken", method = RequestMethod.GET)
    public String previewByToken() {
        return "preview";
    }

    @RequestMapping(value = {"/preview/excel2html", "/preview/handleExcel"}, method = RequestMethod.GET)
    public String handleExcel() {
        return "excel2html";
    }

    @RequestMapping(value = {"/preview/pdf2html", "/preview/handlePdf"}, method = RequestMethod.GET)
    public ModelAndView handlePdf(HttpServletRequest request) {
        String widthStr = UrlParametersHelper.safeGetRequestParameter(request, "width");
        int width = 1000;
        if (htmlWidthList.contains(widthStr)) {
            width = NumberUtils.toInt(widthStr);
        }
        ModelAndView modelAndView = new ModelAndView("pdf2html");
        modelAndView.addObject("width", width);
        return modelAndView;
    }

}
