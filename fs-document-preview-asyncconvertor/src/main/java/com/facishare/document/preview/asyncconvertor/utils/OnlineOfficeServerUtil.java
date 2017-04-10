package com.facishare.document.preview.asyncconvertor.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.ConvertPdf2HtmlEnqueueUtil;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
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
    private static final MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    ConvertPdf2HtmlEnqueueUtil convertPdf2HtmlEnqueueUtil;

    @PostConstruct
    void init() {
        ConfigFactory.getConfig("fs-dps-config", config -> {
            oosServerUrl = config.get("oosServerUrl");
            fscServerUrl = config.get("fscServerUrl");
        });
    }

    public String ConvertPPT2Pdf(String ea, int employeeId, String path, String sg) throws IOException, InterruptedException {
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        if (!Strings.isNullOrEmpty(previewInfo.getPdfFilePath())) {
            return previewInfo.getPdfFilePath();
        }
        String filePath = "";
        int tryCount = 0;
        while (tryCount++ < 10) {
            String json = checkPPT2Pdf(ea, employeeId, path, sg);
            JSONObject jsonObject = JSONObject.parseObject(json);
            if (jsonObject.get("Error") == null) {
                String printUrl = ((JSONObject) jsonObject.get("Result")).getString("PrintUrl");
                byte[] bytes = downloadPdfByPrintUrl(printUrl);
                filePath = savePdfFile(ea, path, bytes);
                break;
            } else
                Thread.sleep(100);
        }
        return filePath;
    }

    public String ConvertWord2Pdf(String ea, int employeeId, String path, String sg) throws IOException, InterruptedException {
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        if (!Strings.isNullOrEmpty(previewInfo.getPdfFilePath())) {
            return previewInfo.getPdfFilePath();
        }
        String filePath = "";
        int tryCount = 0;
        while (tryCount++ < 10) {
            WordConvertInfo wordConvertInfo = checkWord2Pdf(ea, employeeId, path, sg);
            boolean finished = wordConvertInfo.isFinished();
            if (finished) {
                filePath = savePdfFile(ea, path, wordConvertInfo.getBytes());
                break;
            } else
                Thread.sleep(100);
        }
        return filePath;
    }


    private WordConvertInfo checkWord2Pdf(String ea, int employeeId, String path, String sg) {
        log.info("begin check word to pdf!");
        Stopwatch stopwatch = Stopwatch.createStarted();
        String ext = FilenameUtils.getExtension(path);
        String name = SampleUUID.getUUID() + "." + ext;
        String downloadUrl = String.format(fscServerUrl, ea, String.valueOf(employeeId), path, sg, name);
        String src = oosServerUrl + "/oh/wopi/files/@/wFileId?wFileId=" + URLEncoder.encode(downloadUrl);
        String url = oosServerUrl + "/wv/WordViewer/request.pdf?WOPIsrc=" + URLEncoder.encode(src) + "&access_token_ttl=0&z=1%2E0&type=downloadpdf";
        WordConvertInfo docConvertInfo = new WordConvertInfo();
        Request request = new Request.Builder().url(url).build();
        client.syncExecute(request, new SyncCallback() {
            @Override
            public Object response(Response response) {
                try {
                    String contentType = response.header("Content-Type");
                    log.info("contentType:{}", contentType);
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
        log.info("end check word to pdf!cost:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
        return docConvertInfo;
    }

    private byte[] downloadPdfByPrintUrl(String printUrl) {
        String url = oosServerUrl + "/p" + printUrl.substring(1);
        log.info("post url:{}", url);
        return client.getBytes(url);
    }

    private String checkPPT2Pdf(String ea, int employeeId, String path, String sg) {
        log.info("begin check ppt to pdf!");
        Stopwatch stopwatch = Stopwatch.createStarted();
        String ext = FilenameUtils.getExtension(path);
        String name = SampleUUID.getUUID() + "." + ext;
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
        stopwatch.stop();
        String resultJson = object.toString();
        log.info("result:{}", resultJson);
        log.info("end check ppt to pdf!cost:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
        return object.toString();
    }

    @Getter
    @Setter
    private class WordConvertInfo {
        //当response的返回头为application/pdf,证明转换完毕,finished为true，object为pdf的bytes
        private boolean finished;
        private byte[] bytes;
    }

    private String savePdfFile(String ea, String path, byte[] bytes) throws IOException {
        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        String dataDir = previewInfo.getDataDir();
        String fileName = SampleUUID.getUUID() + ".pdf";
        String filePath = FilenameUtils.concat(dataDir, fileName);
        FileUtils.writeByteArrayToFile(new File(filePath), bytes);
        previewInfoDao.savePdfFile(ea, path, filePath);
        convertPdf2HtmlEnqueueUtil.enqueue(ea, path);
        return filePath;
    }

}
