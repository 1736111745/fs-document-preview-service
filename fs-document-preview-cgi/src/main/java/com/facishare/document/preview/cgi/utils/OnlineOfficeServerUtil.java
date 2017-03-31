package com.facishare.document.preview.cgi.utils;

import com.fxiaoke.common.http.handler.SyncCallback;
import com.fxiaoke.common.http.spring.OkHttpSupport;
import com.github.autoconf.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.UUID;

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
        String ext = FilenameUtils.getExtension(path).toLowerCase();
        String name = UUID.randomUUID() + "." + ext;
        String downloadUrl = ext.contains("ppt") ? generateDownloadUrlForPPT(ea, employeeId, path, sg, name)
                : generateDownloadUrlForWordAndPdf(ea, employeeId, path, sg, name);
        log.info("begin down load file from oos,url:{}", downloadUrl);
        Request request = new Request.Builder().url(downloadUrl).header("Connection", "close").build();
        Object object = client.syncExecute(request, new SyncCallback() {
            @Override
            public Object response(Response response) {
                try {
                    return response.body().bytes();
                }
                catch (Exception e)
                {
                    log.warn("exception:",e);
                    return  null;
                }
                finally {
                    log.info("response.status:{}",response.code());
                }
            }
        });
        return (byte[]) object;
    }

    private String generateDownloadUrlForWordAndPdf(String ea, int employeeId, String path, String sg, String name) {
        String downloadUrl = String.format(fscServerUrl, ea, String.valueOf(employeeId), path, sg, name);
        String src = oosServerUrl + "/oh/wopi/files/@/wFileId?wFileId=" + URLEncoder.encode(downloadUrl);
        String postUrl = oosServerUrl + "/wv/WordViewer/request.pdf?WOPIsrc=" + URLEncoder.encode(src) + "&type=downloadpdf";
        return postUrl;
    }

    private String generateDownloadUrlForPPT(String ea, int employeeId, String path, String sg, String name) {
        String downloadUrl = String.format(fscServerUrl, ea, String.valueOf(employeeId), path, sg, name);
        String src = oosServerUrl + "/oh/wopi/files/@/wFileId?wFileId=" + URLEncoder.encode(downloadUrl);
        String pid = "WOPIsrc=" + URLEncoder.encode(src);
        String postUrl = oosServerUrl + "/p/printhandler.ashx?Pid=" + URLEncoder.encode(pid);
        return postUrl;
    }
}
