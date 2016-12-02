package com.facishare.document.preview.cgi.convertor;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;


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

    private String readFile(File file) throws IOException {
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
        String regex = "<head>[\\s\\S]*</head>";
        html = html.replaceAll(regex, "");
        html = html.replaceAll("<script[^>]*>[\\d\\D]*?</script>","");//去掉script
        html = html.replace("<!DOCTYPE html><html>", "").trim();
        html = html.replace("</html>", "").trim();
        html = html.replace("<body>", "").trim();
        html = html.replace("</body>", "").trim();
        html = html.replace("./js", "./" + dirName + "/js");
        css="table, tbody, tfoot, thead, tr, th, td {border: 1px solid #dddddd;}"+css;
        html = "<style>" + css + "</style>" + html;
        FileUtils.writeStringToFile(file, html, false);
    }
}