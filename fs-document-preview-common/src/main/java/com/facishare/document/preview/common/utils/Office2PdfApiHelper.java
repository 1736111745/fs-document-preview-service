package com.facishare.document.preview.common.utils;

import com.facishare.document.preview.common.model.RestResponse;
import com.fxiaoke.common.http.handler.SyncCallback;
import com.fxiaoke.common.http.spring.OkHttpSupport;
import com.github.autoconf.ConfigFactory;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuq on 2017/4/14.
 */
@Component
@Slf4j
public class Office2PdfApiHelper {

    @ReloadableProperty("oosServerUrl")
    private String oosServerUrl = "";
    @Resource(name = "httpClientSupport")
    private OkHttpSupport client;

    @PostConstruct
    void init() {
        ConfigFactory.getConfig("fs-dps-config", config -> {
            oosServerUrl = config.get("oosServerUrl");
        });
    }

    public int getPageCount(String filePath) throws IOException {
        int pageCount = 0;
        RestResponse restResponse = callApi("GetPageCount", filePath, null);
        byte[] bytes = restResponse.getBytes();
        if (bytes != null) {
            String pageCountStr = IOUtils.toString(bytes, "UTF-8");
            pageCount = NumberUtils.toInt(pageCountStr, 0);
        }
        return pageCount;
    }

    public byte[] getPdfBytes(String filePath, int page) {
        byte[] pdfContents = null;
        RestResponse restResponse = callApi("GetPdfBytes", filePath, "page=" + page);
        if (restResponse.getContentType().contains("application/pdf")) {
            pdfContents = restResponse.getBytes();
        }
        return pdfContents;
    }


    private RestResponse callApi(String method, String filePath, String params) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        RestResponse restResponse = new RestResponse();
        RequestBody requestBody = createRequestBody(filePath);
        String postUrl = oosServerUrl + "/Api/" + method;
        if (!Strings.isNullOrEmpty(params)) {
            postUrl = postUrl + "?" + params;
        }
        Request request = new Request.Builder().url(postUrl).post(requestBody).build();
        try {
            client.syncExecute(request, new SyncCallback() {
                @Override
                public Object response(Response response) throws Exception {
                    String contentType = response.header("Content-Type");
                    log.info("contentType:{}", contentType);
                    log.info("response.status:{}", response.code());
                    byte[] bytes = response.body().bytes();
                    restResponse.setBytes(bytes);
                    restResponse.setContentType(contentType);
                    return bytes;
                }
            });
        } catch (Exception ex) {
            log.error("call method:{},path:{},happened exception!",method, filePath, ex);
        }
        stopwatch.stop();
        log.info("call api:{},cost:{}", postUrl, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return restResponse;
    }

    private RequestBody createRequestBody(String filePath) {
        String fileName = SampleUUID.getUUID() + "." + FilenameUtils.getExtension(filePath);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/office"), new File(filePath));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, fileBody)
                .build();
        return requestBody;
    }
}