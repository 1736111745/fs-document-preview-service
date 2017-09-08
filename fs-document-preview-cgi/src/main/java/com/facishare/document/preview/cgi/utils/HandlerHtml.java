package com.facishare.document.preview.cgi.utils;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuq on 2017/4/20.
 */
@Slf4j
public class HandlerHtml {

  public static void process(String filePath, int page) throws IOException {
    try {
      String encoding = "UTF-8";
      File htmlFile = new File(filePath);
      long fileSize = htmlFile.length();
      log.info("begin handle html,filePath:{},page:{},file size:{}", filePath, page, fileSize);
      if (fileSize > 1024 * 1024 * 10) {
        //如果生成的html大于10m，就不给个错误提示
        String html = "<h3>该工作表数据异常，请检查工作表的行数或者列数是否过大！</h3>";
        FileUtils.writeByteArrayToFile(htmlFile, html.getBytes(encoding), false);
        return;
      }
      Document document = Jsoup.parse(htmlFile, encoding);
      Element body = document.body();
      Elements links = document.head().getElementsByTag("link");
      links.remove();
      Elements images = body.getElementsByTag("img");
      for (int i = 0; i < images.size(); i++) {
        Element image = images.get(i);
        if (imageNeedRemove(image)) {
          image.remove();
        }
      }
      FileUtils.writeByteArrayToFile(htmlFile, document.html().getBytes(encoding), false);
    } catch (Exception e) {
      log.info("handle html happened error!", e);
    }
  }


  private static boolean imageNeedRemove(Element image) {
    boolean flag = false;
    String imgHeight = image.attr("height");
    String imgWidth = image.attr("width");
    if (Strings.isNullOrEmpty(imgHeight) && Strings.isNullOrEmpty(imgWidth)) {
      int width = NumberUtils.toInt(imgWidth);
      int height = NumberUtils.toInt(imgHeight);
      if (width < 10 || height < 10) {
        flag = true;
      }
    }
    return flag;
  }

  public static void main(String[] args) throws Exception {
  }
}
