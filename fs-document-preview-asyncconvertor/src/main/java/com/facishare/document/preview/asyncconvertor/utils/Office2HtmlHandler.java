package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.ConvertPdf2HtmlEnqueueUtil;
import com.facishare.document.preview.common.utils.DocPageInfoHelper;
import com.facishare.document.preview.common.utils.SampleUUID;
import com.fxiaoke.common.http.handler.SyncCallback;
import com.fxiaoke.common.http.spring.OkHttpSupport;
import com.github.autoconf.ConfigFactory;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuq on 2017/4/13.
 */
@Component
@Slf4j
public class Office2HtmlHandler {
    private String oosServerUrl = "";
    @Resource(name = "httpClientSupport")
    private OkHttpSupport client;
    @Autowired
    PreviewInfoDao previewInfoDao;
    @Autowired
    ConvertPdf2HtmlEnqueueUtil convertPdf2HtmlEnqueueUtil;

    @PostConstruct
    void init() {
        ConfigFactory.getConfig("fs-dps-config", config -> {
            oosServerUrl = config.get("oosServerUrl");
        });
    }

    public String ConvertOffice2Pdf(String ea, String path) throws Exception {

        PreviewInfo previewInfo = previewInfoDao.getInfoByPath(ea, path);
        if (!Strings.isNullOrEmpty(previewInfo.getPdfFilePath())) {
            return previewInfo.getPdfFilePath();
        }
        String originalFilePath = previewInfo.getOriginalFilePath();
        String ext = FilenameUtils.getExtension(path);
        String dataDir = previewInfo.getDataDir();
        String fileName = SampleUUID.getUUID() + ".pdf";
        String filePath = FilenameUtils.concat(dataDir, fileName);
        RequestBody requestBody = createRequestBody(originalFilePath);
        String postUrl = oosServerUrl + "?ext=" + ext;
        log.info("ea:{},path:{},post url:{}", ea, path, postUrl);
        Request request = new Request.Builder().url(postUrl).post(requestBody).build();
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            client.syncExecute(request, new SyncCallback() {
                @Override
                public Object response(Response response) throws Exception {
                    String contentType = response.header("Content-Type");
                    log.info("contentType:{}", contentType);
                    log.info("response.status:{}", response.code());
                    byte[] bytes = response.body().bytes();
                    if (contentType.contains("application/pdf")) {
                        savePdfFile(ea, path, bytes, filePath);
                    }
                    return bytes;
                }
            });
        } catch (Exception ex) {
            log.error("convert2pdf  path:{},happened exception!", path, ex);
        }
        stopwatch.stop();
        log.info("convert2pdf file length:{}, cost:{}ms", previewInfo.getDocSize(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return filePath;
    }

    private void savePdfFile(String ea, String path, byte[] bytes, String filePath) throws Exception {
        FileUtils.writeByteArrayToFile(new File(filePath), bytes);
        int pageCount = DocPageInfoHelper.getPageInfo(filePath).getPageCount();
        previewInfoDao.savePdfFile(ea, path, filePath, pageCount);
        convertPdf2HtmlEnqueueUtil.enqueue(ea, path);
    }


    private static RequestBody createRequestBody(String filePath) {
        String fileName = SampleUUID.getUUID();
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/office"), new File(filePath));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, fileBody)
                .build();
        return requestBody;
    }

    public static void main(String[] args) throws IOException {
        String originalFilePath = "/Users/liuq/Downloads/documents/docfiles/无法显示图片文件 (1).doc";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = createRequestBody(originalFilePath);
        //2.3 获取请求体
        String postUrl = "http://office2pdf.nsvc.foneshare.cn/Api/ConvertOffice2Pdf?ext=doc";
        log.info("post url:{}", postUrl);
        Request request = new Request.Builder().url(postUrl)
                .post(requestBody)
                .build();

        //step 4： 建立联系 创建Call对象
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // TODO: 17-1-4  请求失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}
