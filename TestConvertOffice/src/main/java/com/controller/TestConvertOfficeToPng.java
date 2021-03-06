package com.controller;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.model.ConvertResult;
import com.fxiaoke.common.http.handler.SyncCallback;
import com.fxiaoke.common.http.spring.OkHttpSupport;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @author Andy
 */
@Slf4j
@Controller
@RequestMapping(value = "/TestConvertOfficeToPng")
public class TestConvertOfficeToPng {

//  private String officeConvertorServerUrl = "http://172.31.101.246:37924";
  private String officeConvertorServerUrl = "http://localhost:8023";

  @Autowired
  private OkHttpSupport client;

  @ResponseBody
  @RequestMapping(value = "/TestConvertAllPageToPng", method = RequestMethod.POST, produces = "application/json")
  public String TestConvertAllPageToPng(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) throws IOException {
    byte[] data = toByteArray(file.getInputStream());
    data = convertOffice2Png(path, data);
    if (data.length > 0) {
      FileOutputStream os = new FileOutputStream("C:\\Users\\anyl9356\\Documents\\TestTwo\\test.zip");
      os.write(data);
      os.close();
      return JSON.toJSONString("succeed");
    }
    return JSON.toJSONString("fail");
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

  public byte[] convertOffice2Png(String npath, byte[] data) {
    String params = "path=" + URLEncoder.encode(npath);
    Object obj = callApi(officeConvertorServerUrl, "ConvertOffice2PngByStream", params, data);
    if (obj instanceof String) {
      return null;
    } else {
      byte[] bytes = (byte[]) obj;
      return bytes;
    }
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
          log.info("request response?????? {}:{}", response.code(), body.length);
          return body;
        }
      }
    });
  }

  @ResponseBody
  @RequestMapping(value = "/TestConvertOnePageToPng", method = RequestMethod.POST, produces = "application/json")
  public String TestConvertOnePageToPng(@RequestParam("path") String path, int page, @RequestParam("file") MultipartFile file) throws IOException {
    byte[] data = toByteArray(file.getInputStream());
    boolean success = convertOffice2Png(path, page, data);
    return JSON.toJSONString(success);
  }

  public boolean convertOffice2Png(String filePath, int page, byte[] data) throws IOException {
    String params = "path=" + URLEncoder.encode(filePath) + "&page=" + page;
    Object obj = callApi(officeConvertorServerUrl, "ConvertOnePageOffice2PngByStream", params, data);
    if (obj instanceof String) {
      ConvertResult convertResult = JSON.parseObject((String) obj, ConvertResult.class);
      return convertResult.isSuccess();
    } else {
      byte[] bytes = (byte[]) obj;
      File file = new File(filePath);
      String dir = file.getParent();
      String pngFileName = (page + 1) + ".png";
      File pngFile = new File(FilenameUtils.concat(dir, pngFileName));
      FileUtils.writeByteArrayToFile(pngFile, bytes);
      return true;
    }
  }

}
