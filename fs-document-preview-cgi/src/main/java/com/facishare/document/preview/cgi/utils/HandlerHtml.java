package com.facishare.document.preview.cgi.utils;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuq on 2017/4/20.
 */
public class HandlerHtml {

    public static void process(String filePath, int page) throws IOException {
        String encoding="UTF-8";
        Document document = Jsoup.parse(new File(filePath), encoding);
        Element head = document.head();
        Element body = document.body();
        String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String dirName=FilenameUtils.getName(baseDir);
        String imageDirName = page + "_files";
        String imageDir = FilenameUtils.concat(baseDir, imageDirName);
        Elements images = body.getElementsByTag("img");
        for (int i = 0; i < images.size(); i++) {
            Element image = images.get(i);
            String src = image.attr("src");
            if (!Strings.isNullOrEmpty(src)) {
                String imageFilePath = FilenameUtils.concat(baseDir, src);
                String ext = FilenameUtils.getExtension(imageFilePath);
                String newImageName = page + "_img_" + i + "." + ext;
                String newImageFilPath = FilenameUtils.concat(baseDir, newImageName);
                FileUtils.moveFile(new File(imageFilePath), new File(newImageFilPath));
                image.attr("src", "./" + dirName + "/" + newImageName);
            }
        }
        FileUtils.deleteQuietly(new File(imageDir));
        Elements styles = head.getElementsByTag("style");
        StringBuilder styleBuilder = new StringBuilder();
        String styleHtml = "";
        for (int i = 0; i < styles.size(); i++) {
            Element style = styles.get(i);
            styleBuilder.append(style.html());
        }
        if (styleBuilder.length() > 0) {
            styleHtml = "<style>" + styleBuilder.toString() + "</style>";
        }
        String html = styleHtml + body.html();
        html = html.replace("\n", "");
        FileUtils.writeByteArrayToFile(new File(filePath),html.getBytes(encoding),false);
    }

    public static void main(String[] args) throws IOException {

        process("/Users/liuq/Desktop/0.html", 0);
    }

}
