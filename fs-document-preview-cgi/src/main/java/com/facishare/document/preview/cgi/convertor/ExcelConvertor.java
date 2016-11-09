package com.facishare.document.preview.cgi.convertor;

import com.google.common.base.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;

import static com.facishare.document.preview.cgi.model.FileTokenFields.fileName;

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
            return dirName + "/" + fileName;
        }
        return htmlFilePath;
    }

    @Override
    public String convert(int page1, int page2, String filePath, String folder,int width) throws Exception {
        //// TODO: 2016/11/4 excel的单页预览要考虑样式等问题的打包 因为最终给用户的是一个二进制 
        throw new Exception("Not Support!");
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