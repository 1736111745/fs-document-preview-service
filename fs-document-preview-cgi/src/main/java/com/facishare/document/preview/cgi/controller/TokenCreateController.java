package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.model.CreatePreviewShareTokens;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.utils.AES256Utils;
import com.fxiaoke.common.http.spring.OkHttpSupport;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author liuquan
 * @date 2021-04-08  16:35
 */
@Controller
@RequestMapping("/")
@Slf4j
public class TokenCreateController {
  @Autowired
  OkHttpSupport okHttpSupport;
  @RequestMapping(value = "/createToken",method = RequestMethod.POST)
  @ResponseBody
  public CreatePreviewShareTokens.Result createPreviewToken(@RequestBody CreatePreviewShareTokens.Arg arg, HttpServletRequest request) throws IOException {
    EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
    arg.setEa(employeeInfo.getEa());
    arg.setEmployeeId(employeeInfo.getEmployeeId());
    Map<String, String> resultMap = Maps.newHashMap();
    String ea = arg.ea;
    int employeeId = arg.employeeId;
    List<String> pathList = arg.pathList;
    String sg = Strings.isNullOrEmpty(arg.securityGroup) ? "" : arg.securityGroup;
    pathList.forEach(item -> {
      String fileId = "";
      try {
        fileId = AES256Utils.encode(Joiner.on(':').join(ea, employeeId, item, sg, System.currentTimeMillis()));
      } catch (Exception e) {
        log.error("createPreviewShareTokens Exception {},{}", ea, item, e);
      }
      resultMap.put(item, fileId);
    });
    return new CreatePreviewShareTokens.Result(resultMap);
  }


}
