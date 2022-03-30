package com.facishare.document.preview.office2pdf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author liuquan
 * @date 2020-10-27  16:36
 */
@Controller
public class IndexController {

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index(){
    return "index";
  }
}
