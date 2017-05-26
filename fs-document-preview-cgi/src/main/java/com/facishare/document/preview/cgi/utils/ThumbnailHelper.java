package com.facishare.document.preview.cgi.utils;

import com.fxiaoke.common.http.handler.SyncCallback;
import com.fxiaoke.common.http.spring.OkHttpSupport;
import com.github.autoconf.ConfigFactory;
import com.google.common.collect.Lists;
import com.squareup.pollexor.Thumbor;
import com.squareup.pollexor.ThumborUrlBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * Created by liuq on 2017/4/20.
 */
@Slf4j
@Component
public class ThumbnailHelper {
  @Resource(name = "httpClientSupport")
  private OkHttpSupport client;
  private static String thumborServiceUrl = "";

  static {
    ConfigFactory.getInstance()
                 .getConfig("fs-dps-config", config -> thumborServiceUrl = config.get("thumborServiceUrl"));
  }

  public boolean doThumbnail(byte[] data, int toWidth, int toHeight, File thumbPngFile) {
    boolean success = false;
    try {
      MediaType mediaType = MediaType.parse("application/octet-stream;");
      RequestBody body = MultipartBody.create(mediaType, data);
      Thumbor thumbor = Thumbor.create(thumborServiceUrl);
      List<String> filters = Lists.newArrayList();
      filters.add(ThumborUrlBuilder.quality(80));
      String[] filterArray = filters.toArray(new String[filters.size()]);
      String url = thumbor.buildImage("post").filter(filterArray).resize(toWidth, toHeight).smart().toUrlUnsafe();
      log.info("do thumbnail with url:{}", url);
      final Request request = new Request.Builder().post(body).url(url).build();
      Object object = client.syncExecute(request, new SyncCallback() {
        @Override
        public Object response(Response response) throws Exception {
          return response.body().bytes();
        }
      });
      byte[] bytes = (byte[]) object;
      if (bytes != null) {
        log.info("do thumbnail success!");
        FileUtils.writeByteArrayToFile(thumbPngFile, bytes);
        success = true;
      }
    } catch (Exception e) {
      log.error("thumb fail!", e);
    }
    return success;
  }
}
