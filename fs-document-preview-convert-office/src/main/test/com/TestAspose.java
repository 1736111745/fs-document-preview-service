package com;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.model.ConvertResult;
import com.facishare.document.preview.common.model.PageInfo;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class TestAspose {
  static {
    System.setProperty("profile.process","fstest");
  }

  @Autowired
  private OkHttpSupport client;
  private String officeConvertorServerUrl = "http://localhost:8082";

  @Test
  public void convertOffice2PdfTest() throws IOException {
    String filePath="C:\\Users\\anyl9356\\Documents\\TestTwo\\test.ppt";
    System.out.print(convertOffice2Pdf(officeConvertorServerUrl, filePath));
  }

  public boolean convertOffice2Pdf(String url, String filePath) throws IOException {
    String params = "path=" + URLEncoder.encode(filePath);
    byte[] data = FileUtils.readFileToByteArray(new File(filePath));
    Object obj = callApi(url, "ConvertOffice2PdfByStream", params, data);
    if (obj instanceof String) {
      ConvertResult convertResult = JSON.parseObject((String) obj, ConvertResult.class);
      return convertResult.isSuccess();
    } else {
      byte[] bytes = (byte[]) obj;
      File file = new File(filePath);
      String dir = file.getParent();
      String pdfFileName = "convertAfter" + ".pdf";
      File pdfFile = new File(FilenameUtils.concat(dir, pdfFileName));
      FileUtils.writeByteArrayToFile(pdfFile, bytes);
      return true;
    }
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

  @Test
  public void convertExcel2HtmlTest() throws IOException {
    String filePath2="C:\\Users\\anyl9356\\Documents\\TestTwo\\test.xls";
    System.out.print(convertExcel2Html(filePath2, 1));
  }

  public boolean convertExcel2Html(String filePath, int page) throws IOException {
    String params = "path=" + URLEncoder.encode(filePath) + "&page=" + page;
    byte[] data = FileUtils.readFileToByteArray(new File(filePath));
    Object obj = callApi(officeConvertorServerUrl, "ConvertExcel2HtmlByStream", params, data);

    if (obj instanceof String) {
      ConvertResult convertResult = JSON.parseObject((String) obj, ConvertResult.class);
      return convertResult.isSuccess();
    } else {
      byte[] bytes = (byte[]) obj;
      File file = new File(filePath);
      String dir = file.getParent();
      String htmlFileName = page + ".html";
      File htmlFile = new File(FilenameUtils.concat(dir, htmlFileName));
      FileUtils.writeByteArrayToFile(htmlFile, bytes);
      return true;
    }
  }

  @Test
  public void convertOffice2PngTest() throws IOException {
    String filePath3="C:\\Users\\anyl9356\\Documents\\TestTwo\\test.ppt";
    System.out.print(convertOffice2Png(filePath3, 1));
  }

  public boolean convertOffice2Png(String filePath, int page) throws IOException {
    String params = "path=" + URLEncoder.encode(filePath) + "&page=" + page;
    byte[] data = FileUtils.readFileToByteArray(new File(filePath));

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

  @Test
  public void convertOnePageOffice2PdfTest() throws IOException {
    String filePath3="C:\\Users\\anyl9356\\Documents\\TestTwo\\test.ppt";
    System.out.print(convertOffice2Pdf(filePath3, 1));
  }

  public boolean convertOffice2Pdf(String filePath, int page) throws IOException {
    String params = "path=" + URLEncoder.encode(filePath) + "&page=" + page;
    byte[] data = FileUtils.readFileToByteArray(new File(filePath));
    Object obj = callApi(officeConvertorServerUrl, "ConvertOnePageOffice2PdfByStream", params, data);
    if (obj instanceof String) {
      ConvertResult convertResult = JSON.parseObject((String) obj, ConvertResult.class);
      return convertResult.isSuccess();
    } else {
      byte[] bytes = (byte[]) obj;
      File file = new File(filePath);
      String dir = file.getParent();
      String pdfFileName = page + ".pdf";
      File pdfFile = new File(FilenameUtils.concat(dir, pdfFileName));
      FileUtils.writeByteArrayToFile(pdfFile, bytes);
      return true;
    }
  }

  @Test
  public void getPageInfoTest() throws IOException {
    String path="test.doc";
    String filePath2="C:\\Users\\anyl9356\\Documents\\TestTwo\\test.doc";
    System.out.print(getPageInfo(path, filePath2));
  }

  public PageInfo getPageInfo(String path, String filePath) throws IOException {
    PageInfo pageInfo;
    String params = "path=" +"";
    byte[] data = FileUtils.readFileToByteArray(new File(filePath));
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


}
