package com.facishare.document.preview.provider.convertor;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by liuq on 16/9/9.
 */
public class ExcelConvertor implements IDocConvertor {
    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {
        String htmlFilePath = ConvertorHelper.toHtml(page1, filePath, baseDir);
        if (!Strings.isNullOrEmpty(htmlFilePath)) {
            File file = new File(htmlFilePath);
            String dirName = FilenameUtils.getBaseName(baseDir);
            handleHtml(file, dirName);
            return dirName + "/" + FilenameUtils.getName(htmlFilePath);
        }
        return htmlFilePath;
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

    public static void main(String[] args) throws IOException {
//        String htmlPath="/nfsshare/normal/dps/201612/11/23/2/dsn79ha4/1.html";
//        String html = readFile(new File(htmlPath)).trim();
//        String imgRegex="<img[^>]+src\\\\s*=\\\\s*['\"]([^'\"]+)['\"][^>]*>";
//        Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
//        Matcher m = p.matcher(html);
//        System.out.println(m.find());
//        System.out.println(m.groupCount());
//        while(m.find()){
//            System.out.println(m.group()+"-------------↓↓↓↓↓↓");
//        }


    }
}