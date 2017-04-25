package com.facishare.document.preview.cgi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by liuq on 16/9/29.
 */
@Controller
@RequestMapping("/")
public class ViewController {

    @RequestMapping(value = "/preview/bypath", method = RequestMethod.GET)
    public String previewByPath() {
        return "preview";
    }

    @RequestMapping(value = "/preview/bytoken", method = RequestMethod.GET)
    public String previewByToken() {
        return "preview";
    }

    @RequestMapping(value ={"/preview/excel2html","/preview/handleExcel"}, method = RequestMethod.GET)
    public String handleExcel() {
        return "excel2html";
    }

    @RequestMapping(value ={"/preview/pdf2html,/preview/handlePdf"}, method = RequestMethod.GET)
    public String handlePdf() {
        return "pdf2html";
    }

}
