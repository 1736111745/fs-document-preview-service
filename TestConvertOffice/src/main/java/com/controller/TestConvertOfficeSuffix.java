package com.controller;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.model.ConvertOldOfficeVersionResult;
import com.fxiaoke.common.http.handler.SyncCallback;
import com.fxiaoke.common.http.spring.OkHttpSupport;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @author Andy
 */

@Slf4j
@Controller
@RequestMapping(value = "/TestConvertOfficeSuffix")
public class TestConvertOfficeSuffix {

  private String officeConvertorServerUrl = "http://localhost:8082";
  @Autowired
  private OkHttpSupport client;

  @ResponseBody
  @RequestMapping(value = "/TestConvertSuffix", method = RequestMethod.POST, produces = "application/json")
  public String TestConvertSuffix(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) throws IOException {
    byte[] data = toByteArray(file.getInputStream());
    ConvertOldOfficeVersionResult result = convertFile(path);
    return JSON.toJSONString(result);
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

  public ConvertOldOfficeVersionResult convertFile(String filePath) throws IOException {
    ConvertOldOfficeVersionResult result;
    String params = "path=" + URLEncoder.encode(filePath);
    byte[] data = FileUtils.readFileToByteArray(new File(filePath));
    Object obj = callApi(officeConvertorServerUrl, "ConvertFileByStream", params, data);
    if (obj instanceof String) {
      result = JSON.parseObject((String) obj, ConvertOldOfficeVersionResult.class);
    } else {
      result = new ConvertOldOfficeVersionResult();
      byte[] bytes = (byte[]) obj;
      result.setErrorMsg("");
      result.setSuccess(true);
      String newFilePath = filePath + "x";
      try {
        FileUtils.writeByteArrayToFile(new File(newFilePath), bytes);
      } catch (IOException e) {
      }
      result.setNewFilePath(newFilePath);
    }
    return result;
  }

  public Object callApi(String url, String method, String params, byte[] data) {
    String postUrl = url + "/Api/Office/" + method;
    if (Strings.isNotEmpty(params)) {
      postUrl = postUrl + "?" + params;
    }
    RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), data);
    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", "file", fileBody).build();
    Request request = new Request.Builder().url(postUrl).post(requestBody).build();
    return client.syncExecute(request, new SyncCallback() {
      @Override
      public Object response(Response response) throws Exception {
        if (response.header("Content-Type").contains("json")) {
          Object obj = response.body().string();
          log.info("response.json:{}", obj);
          return obj;
        } else {
          log.info("response.status:{}", response.code());
          byte[] body = response.body().bytes();
          log.info("request response长度 {}:{}", response.code(), body.length);
          return body;
        }
      }
    });
  }

}
