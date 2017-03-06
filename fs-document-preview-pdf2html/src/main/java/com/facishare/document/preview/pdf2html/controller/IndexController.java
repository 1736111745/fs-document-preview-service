package com.facishare.document.preview.pdf2html.controller;

import lombok.extern.slf4j.Slf4j;
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
    @RequestMapping(method = RequestMethod.GET)
    public String index() throws Exception {
        return "index";
    }
}