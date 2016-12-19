package com.facishare.document.preview.provider.controller;

import com.facishare.document.preview.provider.convertor.DocConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by liuq on 2016/12/14.
 */
@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    DocConvertor docConvertor;
    @RequestMapping(method = RequestMethod.GET)
    public String index() throws Exception {
        //String filePath = "/Users/liuq/Downloads/a.docx";
        //docConvertor.doConvert("a.docx", filePath, 1);
        return "index";
    }
}