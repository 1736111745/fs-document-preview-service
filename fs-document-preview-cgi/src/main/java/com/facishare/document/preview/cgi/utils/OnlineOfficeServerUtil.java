package com.facishare.document.preview.cgi.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.common.utils.SampleUUID;
import com.fxiaoke.common.http.handler.SyncCallback;
import com.fxiaoke.common.http.spring.OkHttpSupport;
import com.github.autoconf.ConfigFactory;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuq on 2017/3/29.
 */
@Component
@Slf4j
public class OnlineOfficeServerUtil {
    private String oosServerUrl = "";
    private String fscServerUrl = "";
    @Resource(name = "httpClientSupport")
    private OkHttpSupport client;
    private static final MediaType JSONType = MediaType.parse("application/json; charset=utf-8");

    @PostConstruct
    void init() {
        ConfigFactory.getConfig("fs-dps-config", config -> {
            oosServerUrl = config.get("oosServerUrl");
            fscServerUrl = config.get("fscServerUrl");
        });
    }

    public byte[] downloadPdfFile(String ea, int employeeId, String path, String sg) throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String ext = FilenameUtils.getExtension(path).toLowerCase();
        String name = SampleUUID.getUUID() + "." + ext;
        return ext.contains("ppt") ? convertPPT2Pdf(ea, employeeId, path, sg, name) : convertDoc2Pdf(ea, employeeId, path, sg, name);
    }


    //todo:doc转pdf是异步的，每次都要请求下看下content-type是否为pdf，如果是就说下载完毕

    private byte[] convertDoc2Pdf(String ea, int employeeId, String path, String sg, String name) throws InterruptedException {
        String downloadUrl = String.format(fscServerUrl, ea, String.valueOf(employeeId), path, sg, name);
        String src = oosServerUrl + "/oh/wopi/files/@/wFileId?wFileId=" + URLEncoder.encode(downloadUrl);
        String postUrl = oosServerUrl + "/wv/WordViewer/request.pdf?WOPIsrc=" + URLEncoder.encode(src) + "&type=accesspdf";
        byte[] bytes = null;
        int tryCount = 0;
        while (tryCount++ < 100) {
            DocConvertInfo docConvertInfo = checkDocConvertPdf(postUrl);
            if (docConvertInfo.finished) {
                bytes = docConvertInfo.getBytes();
                break;
            } else
                Thread.sleep(200);
        }
        return bytes;
    }


    private DocConvertInfo checkDocConvertPdf(String postUrl) {
        DocConvertInfo docConvertInfo = new DocConvertInfo();
        Request request = new Request.Builder().url(postUrl).build();
        client.syncExecute(request, new SyncCallback() {
            @Override
            public Object response(Response response) {
                try {
                    String contentType = response.header("Content-Type");
                    log.info("contentType:{}",contentType);
                    byte[] bytes = response.body().bytes();
                    if (contentType.contains("application/pdf")) {
                        docConvertInfo.setFinished(true);
                        docConvertInfo.setBytes(bytes);
                    }
                    return bytes;
                } catch (Exception e) {
                    log.warn("exception:", e);
                    return null;
                } finally {
                    log.info("response.status:{}", response.code());
                }
            }
        });
        return docConvertInfo;
    }


    private byte[] convertPPT2Pdf(String ea, int employeeId, String path, String sg, String name) throws InterruptedException {
        byte[] bytes = null;
        int tryCount = 0;
        String printUrl = "";
        while (tryCount++ < 100) {
            String json = checkPPTPrintPdf(ea, employeeId, path, sg, name);
            JSONObject jsonObject = JSON.parseObject(json);
            if (jsonObject.get("Error") == null) {
                printUrl = ((JSONObject) jsonObject.get("Result")).getString("PrintUrl");
                log.info("print url:{}", printUrl);
                break;
            } else
                Thread.sleep(200);
        }
        if (!Strings.isNullOrEmpty(printUrl)) {
            String url = oosServerUrl + "/p" + printUrl.substring(1);
            log.info("post url:{}", url);
            bytes = client.getBytes(url);
        }
        return bytes;
    }

    private String checkPPTPrintPdf(String ea, int employeeId, String path, String sg, String name) {
        String downloadUrl = String.format(fscServerUrl, ea, String.valueOf(employeeId), path, sg, name);
        String src = oosServerUrl + "/oh/wopi/files/@/wFileId?wFileId=" + URLEncoder.encode(downloadUrl);
        String pid = "WOPIsrc=" + URLEncoder.encode(src);
        Map<String, String> map = new HashMap<>();
        map.put("presentationId", pid);
        String postUrl = oosServerUrl + "/p/ppt/view.svc/jsonAnonymous/Print";
        RequestBody body = RequestBody.create(JSONType, JSON.toJSONString(map));
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        Object object = client.syncExecute(request, new SyncCallback() {
            @Override
            public Object response(Response response) {
                try {
                    return response.body().string();
                } catch (Exception e) {
                    log.warn("exception:", e);
                    return null;
                } finally {
                    log.info("response.status:{}", response.code());
                }
            }
        });
        String resultJson = object.toString();
        log.info("result:{}", resultJson);
        return object.toString();
    }

    @Getter
    @Setter
    private class DocConvertInfo {
        //当response的返回头为application/pdf,证明转换完毕,finished为true，object为pdf的bytes
        private boolean finished;
        private byte[] bytes;
    }
}
