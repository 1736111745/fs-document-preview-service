package com.facishare.document.preview.provider.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by liuq on 2016/12/14.
 */
@Controller
@RequestMapping("/")
public class IndexController {
//    @Autowired
//    DocConvertor docConvertor;
//    @Autowired
//    DocPageInfoHelper docPageInfoHelper;
    @RequestMapping(method = RequestMethod.GET)
    public String index() throws Exception {
//        String filePath = "/Users/liuq/Downloads/a.docx";
//        docPageInfoHelper.GetPageInfo(filePath);
//        docConvertor.doConvert("a.docx", filePath,0,1);
        return "index";
    }
}