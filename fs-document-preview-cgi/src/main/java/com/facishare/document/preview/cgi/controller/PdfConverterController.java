package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.document.preview.cgi.utils.FileStorageProxy;
import com.facishare.document.preview.cgi.utils.UrlParametersHelper;
import com.facishare.document.preview.common.utils.OfficeApiHelper;
import com.facishare.document.preview.common.utils.SampleUUID;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Controller
@RequestMapping("/")
public class PdfConverterController {

  @Autowired
  FileStorageProxy fileStorageProxy;
  @Autowired
  OfficeApiHelper officeApiHelper;

  @ResponseBody
  @RequestMapping(value = "/pdfconverter", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
  public void pdfconverter(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String path = UrlParametersHelper.safeGetRequestParameter(request, "path");
    if (Strings.isNullOrEmpty(path)) {
      response.setStatus(400);
      return;
    } else {
      EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
      String ea = employeeInfo.getEa();
      int employeeId = employeeInfo.getEmployeeId();
      byte[] officeFile = fileStorageProxy.GetBytesByPath(path, ea, employeeId, "");
      byte[] zipFile = officeApiHelper.convertOffice2Png(path, officeFile);
      if (zipFile != null) {
        String fileName = SampleUUID.getUUID() + ".zip";
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        outputStream.write(zipFile);
        outputStream.flush();
        outputStream.close();
      } else
        response.setStatus(400);
      return;
    }
  }
}
