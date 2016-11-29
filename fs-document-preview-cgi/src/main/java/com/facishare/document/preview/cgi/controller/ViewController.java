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

    @RequestMapping(value = "/preview/handleExcel", method = RequestMethod.GET)
    public String handleExcel() {
        return "preview_excel";
    }

    @RequestMapping(value = "/preview/handlePdf", method = RequestMethod.GET)
    public String handlePdf() {return "preview_pdf";}

    @RequestMapping(value = "/preview/handleWordAndPPT", method = RequestMethod.GET)
    public String handleWordAndPPT() {
        return "preview_word_ppt";
    }

}
