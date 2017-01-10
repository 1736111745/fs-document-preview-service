package com.facishare.document.preview.provider.controller;

import com.facishare.document.preview.provider.convertor.DocConvertor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by liuq on 2016/12/14.
 */
@Controller
@RequestMapping("/")
@Slf4j
public class IndexController {
    @Autowired
    DocConvertor docConvertor;
    @RequestMapping(method = RequestMethod.GET)
    public String index() throws Exception {
//        String filePath = "/Users/liuq/doctest/excel/监理费支付台账.xlsx";
//        docConvertor.doConvert("a.xlsx", filePath,0,1);
//        docConvertor.doConvert("a.xlsx", filePath,1,1);
//        docConvertor.doConvert("a.xlsx", filePath,2,1);
//        docConvertor.doConvert("a.xlsx", filePath,3,1);
//        docConvertor.doConvert("a.xlsx", filePath,4,1);
//        docConvertor.doConvert("a.xlsx", filePath,5,1);
        return "index";
    }
}