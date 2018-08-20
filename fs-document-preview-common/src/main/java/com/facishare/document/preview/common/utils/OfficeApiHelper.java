package com.facishare.document.preview.common.utils;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.model.ConvertOldOfficeVersionResult;
import com.facishare.document.preview.common.model.ConvertResult;
import com.facishare.document.preview.common.model.PageInfo;
import com.github.autoconf.ConfigFactory;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuq on 2017/4/14.
 */
@Component
@Slf4j

public class OfficeApiHelper {
  private String officeConvertorServerUrl = "";
  private String ppt2pdfServerUrl = "";
  private static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS)
                                                                 .readTimeout(60, TimeUnit.SECONDS)
                                                                 .build();

  @PostConstruct
  void init() {
    ConfigFactory.getConfig("fs-dps-config", config -> {
      officeConvertorServerUrl = config.get("officeConvertorServerUrl");
      ppt2pdfServerUrl = config.get("ppt2pdfServerUrl");
    });
  }



  public PageInfo getPageInfo(String path, String filePath) throws IOException {
    PageInfo pageInfo;
    String params = "path=" + filePath;
    byte[] data = FileUtils.readFileToByteArray(new File(filePath));
    String json = (String) callApi(officeConvertorServerUrl, "GetPageInfoByStream", params, data);
    log.info("get page info response json:{}", json);
    if (!Strings.isNullOrEmpty(json)) {
      pageInfo = JSON.parseObject(json, PageInfo.class);
    } else {
      pageInfo = new PageInfo();
      pageInfo.setSuccess(false);
      pageInfo.setErrorMsg("获取文档页码失败，当前文档不可预览!");
      log.error("path:{},filePath:{},获取文档页码失败！", path, filePath);
    }
    return pageInfo;
  }

  public ConvertOldOfficeVersionResult convertFile(String filePath) throws IOException {
    ConvertOldOfficeVersionResult result;
    String params = "path=" + filePath;
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

  public boolean convertExcel2Html(String filePath, int page) throws IOException {
    String params = "path=" + filePath + "&page=" + page;
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


  public boolean convertOffice2Png(String filePath, int page) throws IOException {
    String params = "path=" + filePath + "&page=" + page;
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

  public byte[] convertOffice2Png(String npath,byte[] data) {
    String params = "path=" + npath;
    Object obj = callApi(officeConvertorServerUrl, "ConvertOffice2PngByStream", params, data);
    if (obj instanceof String) {
      return null;
    } else {
      byte[] bytes = (byte[]) obj;
      return bytes;
    }
  }


  public boolean convertOffice2Pdf(String filePath, int page) throws IOException {
    String params = "path=" + filePath + "&page=" + page;
    byte[] data = FileUtils.readFileToByteArray(new File(filePath));
    Object obj = callApi(officeConvertorServerUrl, "ConvertOnePageOffice2PdfByStream", params, data);
    if (obj instanceof String) {
      ConvertResult convertResult = JSON.parseObject((String) obj, ConvertResult.class);
      return convertResult.isSuccess();
    } else {
      byte[] bytes = (byte[]) obj;
      File file = new File(filePath);
      String dir = file.getParent();
      String pdfFileName = filePath + "." + page + ".pdf";
      File pdfFile = new File(FilenameUtils.concat(dir, pdfFileName));
      FileUtils.writeByteArrayToFile(pdfFile, bytes);
      return true;
    }
  }



  public boolean convertOffice2Pdf(String filePath) throws IOException {
    return convertOffice2Pdf(officeConvertorServerUrl, filePath);
  }

  public boolean convertOffice2Pdf(String url, String filePath) throws IOException {
    String params = "path=" + filePath;
    byte[] data = FileUtils.readFileToByteArray(new File(filePath));
    Object obj = callApi(url, "ConvertOffice2PdfByStream", params, data);
    if (obj instanceof String) {
      ConvertResult convertResult = JSON.parseObject((String) obj, ConvertResult.class);
      return convertResult.isSuccess();
    } else {
      byte[] bytes = (byte[]) obj;
      File file = new File(filePath);
      String dir = file.getParent();
      String pdfFileName = filePath + ".pdf";
      File pdfFile = new File(FilenameUtils.concat(dir, pdfFileName));
      FileUtils.writeByteArrayToFile(pdfFile, bytes);
      return true;
    }
  }

  public boolean convertPPT2Pdf(String filePath) throws IOException {

    return convertOffice2Pdf(ppt2pdfServerUrl, filePath);
  }


  private Object callApi(String url, String method, String params, byte[] data) {
    Object obj = null;
    Stopwatch stopwatch = Stopwatch.createStarted();
    String postUrl = url + "/Api/Office/" + method;
    if (!Strings.isNullOrEmpty(params)) {
      postUrl = postUrl + "?" + params;
    }
    RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), data);
    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                                         .addFormDataPart("file", "file", fileBody)
                                                         .build();


    Request request = new Request.Builder().url(postUrl).post(requestBody).build();
    try {
      Response response = client.newCall(request).execute();
      log.info("response.status:{}", response.code());
      if (response.header("Content-Type").contains("json")) {
        obj = response.body().string();
        log.info("response.json:{}", obj);
      } else {
        obj = response.body().bytes();
      }
    } catch (Exception e) {
      log.error("call method:{},path:{},happened exception!", method, params, e);
    }
    stopwatch.stop();
    log.info("call api:{},cost:{}ms", postUrl, stopwatch.elapsed(TimeUnit.MILLISECONDS));
    return obj;
  }


  public static void main(String[] args) throws IOException {
    String json = "";
    String file = "/Users/liuq/Downloads/范雄飞客户对账单_201706220309.xlsx";
    byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
    String postUrl = "http://172.28.2.161:9999/Api/Office/ConvertExcel2HtmlByStream?npath=a.jpg&page=0";
    RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                                         .addFormDataPart("file", "file", fileBody)
                                                         .build();


    Request request = new Request.Builder().url(postUrl).post(requestBody).build();
    try {
      Response response = client.newCall(request).execute();
      log.info("response.status:{}", response.code());
      if (response.code() == 200) {
        byte[] data = response.body().bytes();
        FileUtils.writeByteArrayToFile(new File("/Users/liuq/Downloads/zip.zip"), data);
        log.info("response.json:{}", json);
      }
    } catch (Exception e) {

    }
  }
}
