package com.facishare.document.preview.provider.convertor;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by liuq on 16/9/9.
 */
public class ExcelConvertor implements IDocConvertor {
    @Autowired
    ConvertorHelper convertorHelper;

    @Override
    public String convert2Svg(String filePath, int startPageIndex, int endPageIndex) throws Exception {
        return null;
    }

    @Override
    public String convert2Png(String filePath, int startPageIndex, int endPageIndex) throws Exception {
        return null;
    }

    @Override
    public String convert2Jpg(String filePath, int startPageIndex, int endPageIndex) throws Exception {
        return null;
    }

    @Override
    public String convert2Html(String filePath, int startPageIndex, int endPageIndex) throws Exception {
        String resultFilePath = convertorHelper.toHtml(filePath, startPageIndex, endPageIndex);
        if (!Strings.isNullOrEmpty(resultFilePath)) {
            File file = new File(resultFilePath);
            String dirName = FilenameUtils.getBaseName(FilenameUtils.getFullPathNoEndSeparator(filePath));
            handleHtml(file, dirName);
        }
        return resultFilePath;
    }

    private static String readFile(File file) throws IOException {
        StringBuffer sb = new StringBuffer();
        LineIterator it = FileUtils.lineIterator(file, "UTF-8");
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                sb.append(line);
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
        return sb.toString();
    }

    private void handleHtml(File file, String dirName) throws Exception {
        String cssFilePath = file.getParent() + "/js/stylesheet.css";
        String css = readFile(new File(cssFilePath)).trim();
        String html = readFile(file).trim();
        //检测image如果超过里面的width为1.0就去掉
        html=remove1pixPic(html);
        String regex = "<head>[\\s\\S]*</head>";
        html = html.replaceAll(regex, "");
        html = html.replaceAll("<script[^>]*>[\\d\\D]*?</script>", "");//去掉script
        html = html.replace("<!DOCTYPE html><html>", "").trim();
        html = html.replace("</html>", "").trim();
        html = html.replace("<body>", "").trim();
        html = html.replace("</body>", "").trim();
        html = html.replace("./js", "./" + dirName + "/js");
        css = "table, tbody, tfoot, thead, tr, th, td {border: 1px solid #dddddd;}" + css;
        html = "<style>" + css + "</style>" + html;
        FileUtils.writeStringToFile(file, html, false);
    }

    private String remove1pixPic(String html) {
        Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher m = p.matcher(html);
        while (m.find()) {
            String img = m.group();
            if (img.contains("width=\"1.0\"")) {
                html = html.replace(img, "");
            }
        }
        return html;
    }


}