package com.controller;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.model.PageInfo;
import com.fxiaoke.common.http.handler.SyncCallback;
import com.fxiaoke.common.http.spring.OkHttpSupport;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;


/**
 * @author Andy
 */
@Slf4j
@Controller
@RequestMapping(value = "/TestGetPageInfo")
public class TestGetPageInfo {

  @Autowired
  private OkHttpSupport client;

  private String officeConvertorServerUrl = "http://172.31.101.246:37924";

  @ResponseBody
  @RequestMapping(value = "/TestGetWordPageInfo", method = RequestMethod.POST, produces="application/json")
  public String TestGetWordPageInfo(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) throws IOException {
    byte[] data=toByteArray(file.getInputStream());
    PageInfo pageInfo=getPageInfo("test.doc",path,data);
    return JSON.toJSONString(pageInfo);
  }

  private static byte[] toByteArray(InputStream in) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024 * 4];
    int n = 0;
    try {
      while ((n = in.read(buffer)) != -1) {
        out.write(buffer, 0, n);
      }
    } catch (IOException e) {

    }
    return out.toByteArray();
  }

  public PageInfo getPageInfo(String path, String filePath,byte[] data) throws IOException {
    PageInfo pageInfo;
    String params = "path=" + URLEncoder.encode(filePath);
    String json = (String) callApi(officeConvertorServerUrl, "GetPageInfoByStream", params, data);
    log.info("get page info response json:{}", json);
    if (!com.google.common.base.Strings.isNullOrEmpty(json)) {
      pageInfo = JSON.parseObject(json, PageInfo.class);
    } else {
      pageInfo = new PageInfo();
      pageInfo.setSuccess(false);
      pageInfo.setErrorMsg("获取文档页码失败，当前文档不可预览!");
      log.error("path:{},filePath:{},获取文档页码失败！", path, filePath);
    }
    return pageInfo;
  }

  public Object callApi(String url,String method,String params,byte[] data){
    String postUrl=url+"/Api/Office/"+method;
    if (Strings.isNotEmpty(params)){
      postUrl=postUrl+"?"+params;
    }
    RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), data);
    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
      .addFormDataPart("file", "file", fileBody)
      .build();
    Request request = new Request.Builder().url(postUrl).post(requestBody).build();
    return client.syncExecute(request, new SyncCallback() {
      @Override
      public Object response(Response response) throws Exception {
        if (response.header("Content-Type").contains("json")) {
          Object obj = response.body().string();
          log.info("response.json:{}", obj);
          return obj;
        } else {
          log.info("response.status:{}" ,response.code());
          byte[] body = response.body().bytes();
          log.info("request response长度 {}:{}", response.code(), body.length);
          return body;
        }
      }
    });
  }

  @ResponseBody
  @RequestMapping(value = "/TestGetPptPageInfo", method = RequestMethod.POST, produces="application/json")
  public String TestGetPptPageInfo(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) throws IOException {
    byte[] data=toByteArray(file.getInputStream());
    PageInfo pageInfo=getPageInfo("test.ppt",path,data);
    return JSON.toJSONString(pageInfo);
  }

  @ResponseBody
  @RequestMapping(value = "/TestGetExcelPageInfo", method = RequestMethod.POST, produces="application/json")
  public String TestGetExcelPageInfo(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) throws IOException {
    byte[] data=toByteArray(file.getInputStream());
    PageInfo pageInfo=getPageInfo("test.xls",path,data);
    return JSON.toJSONString(pageInfo);
  }

  @ResponseBody
  @RequestMapping(value = "/TestGetPdfPageInfo", method = RequestMethod.POST, produces="application/json")
  public String TestGetPdfPageInfo(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) throws IOException {
    byte[] data=toByteArray(file.getInputStream());
    PageInfo pageInfo=getPageInfo("test.pdf",path,data);
    return JSON.toJSONString(pageInfo);
  }

}
