package com.facishare.document.preview.common.utils;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.common.model.ConvertOldOfficeVersionResult;
import com.facishare.document.preview.common.model.ConvertResult;
import com.facishare.document.preview.common.model.PageInfo;
import com.github.autoconf.ConfigFactory;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuq on 2017/4/14.
 */
@Component
@Slf4j
public class OfficeApiHelper {
    private String officeConvertorServerUrl = "";
    private String ppt2pdfServerUrl="";
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @PostConstruct
    void init() {
        ConfigFactory.getConfig("fs-dps-config", config ->
        {
            officeConvertorServerUrl = config.get("officeConvertorServerUrl");
            ppt2pdfServerUrl = config.get("ppt2pdfServerUrl");
        });
    }


    public PageInfo getPageInfo(String path, String filePath) throws IOException {
        PageInfo pageInfo;
        String params = "filepath=" + filePath;
        String json = callApi(officeConvertorServerUrl,"GetPageInfo", params);
        log.info("get page info response json:{}",json);
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
        ConvertOldOfficeVersionResult result = null;
        String params = "filepath=" + filePath;
        String json = callApi(officeConvertorServerUrl,"ConvertFile", params);
        if (!Strings.isNullOrEmpty(json)) {
            result = JSON.parseObject(json, ConvertOldOfficeVersionResult.class);
        }
        return result;
    }

    public boolean convertExcel2Html(String path, String filePath, int page) {
        String params = "filepath=" + filePath + "&page=" + page;
        String json = callApi(officeConvertorServerUrl,"ConvertExcel2Html", params);
        if (!Strings.isNullOrEmpty(json)) {
            ConvertResult convertResult = JSON.parseObject(json, ConvertResult.class);
            return convertResult.isSuccess();
        } else {
            log.error("path:{},filePath:{},page:{},Excel to  Html 转换失败！", path, filePath, page);
            return false;
        }
    }

    public boolean convertOffice2Png(String path, String filePath, int page) {
        String params = "filepath=" + filePath + "&page=" + page;
        String json = callApi(officeConvertorServerUrl,"ConvertOnePageOffice2Png", params);
        if (!Strings.isNullOrEmpty(json)) {
            ConvertResult convertResult = JSON.parseObject(json, ConvertResult.class);
            return convertResult.isSuccess();
        } else {
            log.error("path:{},filePath:{},page:{},office to  png 转换失败！", path, filePath, page);
            return false;
        }
    }

    public boolean convertOffice2Pdf(String path, String filePath, int page) {
        String params = "filepath=" + filePath + "&page=" + page;
        String json = callApi(officeConvertorServerUrl,"ConvertOnePageOffice2Pdf", params);
        if (!Strings.isNullOrEmpty(json)) {
            ConvertResult convertResult = JSON.parseObject(json, ConvertResult.class);
            return convertResult.isSuccess();
        } else {
            log.error("path:{},filePath:{},page:{},PPT to Pdf 转换失败！", path, filePath, page);
            return false;
        }
    }



    public boolean convertOffice2Pdf(String path, String filePath) {
        String params = "filepath=" + filePath;
        String json = callApi(officeConvertorServerUrl,"ConvertOffice2Pdf", params);
        if (!Strings.isNullOrEmpty(json)) {
            ConvertResult convertResult = JSON.parseObject(json, ConvertResult.class);
            return convertResult.isSuccess();
        } else {
            log.error("path:{},filePath:{},转换文档失败！", path, filePath);
            return false;
        }
    }



    public boolean convertPPT2Pdf(String path, String filePath) {

        String params = "filepath=" + filePath;
        String json = callApi(ppt2pdfServerUrl, "ConvertOffice2Pdf", params);
        if (!Strings.isNullOrEmpty(json)) {
            ConvertResult convertResult = JSON.parseObject(json, ConvertResult.class);
            return convertResult.isSuccess();
        } else {
            log.error("path:{},filePath:{},ppt转换pdf档失败！", path, filePath);
            return false;
        }
    }


    private String callApi(String url,String method, String params) {
        String json = "";
        Stopwatch stopwatch = Stopwatch.createStarted();
        String postUrl = url + "/Api/Office/" + method;
        if (!Strings.isNullOrEmpty(params)) {
            postUrl = postUrl + "?" + params;
        }
        Request request = new Request.Builder().url(postUrl).get().build();
        try {
            Response response = client.newCall(request).execute();
            log.info("response.status:{}", response.code());
            if (response.code() == 200) {
                json = response.body().string();
                log.info("response.json:{}", json);
            }
        } catch (Exception e) {
            log.error("call method:{},path:{},happened exception!", method, params, e);
        }
        stopwatch.stop();
        log.info("call api:{},cost:{}ms", postUrl, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return json;
    }
}
