package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.common.utils.SampleUUID;
import com.fxiaoke.common.http.handler.SyncCallback;
import com.fxiaoke.common.http.spring.OkHttpSupport;
import com.github.autoconf.ConfigFactory;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @PostConstruct
    void init() {
        ConfigFactory.getConfig("fs-dps-config", config -> {
            oosServerUrl = config.get("oosServerUrl");
            fscServerUrl = config.get("fscServerUrl");
        });
    }

    public byte[] downloadPdfFile(String ea, int employeeId, String path, String sg) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String ext = FilenameUtils.getExtension(path).toLowerCase();
        String name = SampleUUID.getUUID() + "." + ext;
        String downloadUrl = ext.contains("ppt") ? generateDownloadUrlForPPT(ea, employeeId, path, sg, name)
                : generateDownloadUrlForWordAndPdf(ea, employeeId, path, sg, name);
        log.info("begin download file from oos,url:{}", downloadUrl);
        Request request = new Request.Builder().url(downloadUrl).header("Connection", "close").build();
        Object object = client.syncExecute(request, new SyncCallback() {
            @Override
            public Object response(Response response) {
                try {
                    return response.body().bytes();
                } catch (Exception e) {
                    log.warn("exception:", e);
                    return null;
                } finally {
                    log.info("response.status:{}", response.code());
                }
            }
        });
        stopwatch.stop();
        log.info("download completed!cost:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return (byte[]) object;
    }

    private String generateDownloadUrlForWordAndPdf(String ea, int employeeId, String path, String sg, String name) {
        String downloadUrl = String.format(fscServerUrl, ea, String.valueOf(employeeId), path, sg, name);
        String src = oosServerUrl + "/oh/wopi/files/@/wFileId?wFileId=" + URLEncoder.encode(downloadUrl);
        String postUrl = oosServerUrl + "/wv/WordViewer/request.pdf?WOPIsrc=" + URLEncoder.encode(src) + "&type=accesspdf";
        return postUrl;
    }

    private String generateDownloadUrlForPPT(String ea, int employeeId, String path, String sg, String name) {
        String downloadUrl = String.format(fscServerUrl, ea, String.valueOf(employeeId), path, sg, name);
        String src = URLEncoder.encode(downloadUrl + "&access_token_ttl=0&z=1.0");
        String pid = "WOPIsrc=" + src;
        String postUrl = oosServerUrl + "/p/printhandler.ashx?PV=0&Pid=" + URLEncoder.encode(pid);
        return postUrl;
    }
}
