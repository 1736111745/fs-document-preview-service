package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.asyncconvertor.model.ExecuteResult;
import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by liuq on 2017/3/7.
 */
@Slf4j
@Component
public class Pdf2HtmlHandler {
    @ReloadableProperty("pdf2HtmlTimeout")
    private int pdf2HtmlTimeout = 60;

    public String doConvert(ConvertPdf2HtmlMessage message) throws IOException {
        String dataFilePath = "";
        String filePath = message.getFilePath();
        int page = message.getPage();
        int type = message.getType();
        String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String outPutDir = FilenameUtils.concat(basedDir, "p" + page);
        String[] args = createProcessArgs(filePath, outPutDir, page, type);
        LocalCommandExecutor executor = new LocalCommandExecutor();
        ExecuteResult result = executor.executeCommand(args, pdf2HtmlTimeout);
        log.info("退出码:{},输出内容:{}", result.getExitCode(), result.getExecuteOut());
        if (result.getExitCode() == 0) {
            dataFilePath = handleResult(page, filePath, outPutDir, type);
        }
        if (type == 1) {
            FileUtils.deleteQuietly(new File(filePath));
        }
        return dataFilePath;
    }

    private String[] createProcessArgs(String filePath, String outPutDir, int page, int type) {
        if (type == 1)
            page = 1;
        List<String> args = Lists.newArrayList();
        args.add("pdf2htmlEX");//命令行开始
        args.add("-f");
        args.add(String.valueOf(page));
        args.add("-l");
        args.add(String.valueOf(page));
        args.add("--fit-width");//缩放px
        args.add("1000");
        args.add("--embed-outline");//链接文件单独输出
        args.add("0");
        args.add("--embed-css");
        args.add("0");
        if (type == 2) {
            args.add("--css-filename");
            args.add("css" + page + ".css");
        }
        args.add("--embed-image");
        args.add("0");
        args.add("--bg-format");
        args.add("jpg");
        args.add("--no-drm");
        args.add("1");
        args.add("--process-outline");
        args.add("0");
        args.add("--optimize-text");
        args.add("1");
        args.add("--correct-text-visibility");
        args.add("1");
        args.add("--embed-javascript");//js文件单独引用
        args.add("0");
        args.add("--dest-dir");//输出目录
        args.add(outPutDir);
        args.add(filePath);
        String cmd = StringUtils.join(args, " ");
        return new String[]{"/bin/sh", "-c", cmd};
    }


    private String handleResult(int page, String filePath, String outPutDir, int type) throws IOException {
        String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        String dataFileName = FilenameUtils.getBaseName(filePath) + ".html";
        String dataFilePath = FilenameUtils.concat(outPutDir, dataFileName);
        String pageName = page + ".html";
        String pagePath = FilenameUtils.concat(baseDir, pageName);
        File dataFile = new File(dataFilePath);
        File pageFile = new File(pagePath);
        String dirName = FilenameUtils.getBaseName(baseDir);
        String cssFileName = type == 1 ? FilenameUtils.getBaseName(filePath) + ".css" : "css" + page + ".css";
        String newCssFileName = page + ".css";
        String cssFileFilePath = FilenameUtils.concat(outPutDir, cssFileName);
        String newCssFilePath = FilenameUtils.concat(baseDir, newCssFileName);
        File cssFile = new File(cssFileFilePath);
        cssFile.renameTo(new File(newCssFilePath));
        //处理背景图片
        Path bgPath = Files.list(Paths.get(outPutDir)).filter(f -> f.toFile().getName().startsWith("bg")).findFirst().orElse(null);
        String bgName = "";
        String newBgName = "";
        if (bgPath != null) {
            File bgFile = bgPath.toFile();
            bgName = bgFile.getName();
            newBgName = "bg" + page + ".jpg";
            String newBgFilePath = FilenameUtils.concat(baseDir, newBgName);
            bgFile.renameTo(new File(newBgFilePath));
        }
        handleHtml(dataFile, pageFile, dirName, cssFileName, newCssFileName, bgName, newBgName);
        FileUtils.deleteDirectory(new File(outPutDir));
        return pagePath;
    }

    private void handleHtml(File dataFile, File pageFile, String dirName, String cssName, String newCssName, String bgName, String newBgName) throws IOException {
        String html = FileUtils.readFileToString(dataFile);
        //todo:匹配树。
        html = html.replace("base.min.css", "../static/css/base.min.css");
        html = html.replace("<link rel=\"stylesheet\" href=\"fancy.min.css\"/>", "");
        html = html.replace(cssName, "./" + dirName + "/" + newCssName);
        html = html.replace("<script src=\"compatibility.min.js\"></script>", "");
        html = html.replace("<script src=\"pdf2htmlEX.min.js\"></script>", "");
        html = html.replace("<script>\n" +
                "try{\n" +
                "pdf2htmlEX.defaultViewer = new pdf2htmlEX.Viewer({});\n" +
                "}catch(e){}\n" +
                "</script>", "");
        html = html.replace("<div id=\"sidebar\">\n" +
                "<div id=\"outline\">\n" +
                "</div>\n" +
                "</div>", "");
        html = html.replace("<div class=\"loading-indicator\">", "");
        html = html.replace("<img alt=\"\" src=\"pdf2htmlEX-64x64.png\"/>", "");
        if (!Strings.isNullOrEmpty(bgName)) {
            html = html.replace(bgName, "./" + dirName + "/" + newBgName);
        }
        html = html.replace("\n", "");
        FileUtils.writeByteArrayToFile(pageFile, html.getBytes());
    }
}
