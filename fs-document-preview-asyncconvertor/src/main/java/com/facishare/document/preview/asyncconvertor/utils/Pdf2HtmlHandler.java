package com.facishare.document.preview.asyncconvertor.utils;

import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.facishare.document.preview.common.utils.ThumbnailHelper;
import com.fxiaoke.common.image.SimpleImageInfo;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;
import org.zeroturnaround.process.SystemProcess;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

/**
 * Created by liuq on 2017/3/7.
 */
@Slf4j
@Component
public class Pdf2HtmlHandler {
  @ReloadableProperty("pdf2HtmlTimeout")
  private int pdf2HtmlTimeout = 60;
  @Autowired
  ThumbnailHelper thumbHelper;

  public String doConvert(ConvertPdf2HtmlMessage message) throws IOException {
    String dataFilePath = "";
    String filePath = message.getFilePath();
    int page = message.getPage();
    int type = message.getType();
    int width = message.getPageWidth();
    String basedDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
    String outPutDir = FilenameUtils.concat(basedDir, "p" + page);
    List<String> args = createProcessArgs(filePath, outPutDir, page, type, width);
    StartedProcess startedProcess = new ProcessExecutor().command(args).readOutput(true).start();
    Process pdf2htmlProcess = startedProcess.getProcess();
    try {
      Future<ProcessResult> future = startedProcess.getFuture();
      ProcessResult result = future.get(pdf2HtmlTimeout, TimeUnit.SECONDS);
      if (result.getExitValue() == 0) {
        log.info("pdf2html finished,begin handle result!");
        dataFilePath = handleResult(message.getEa(), page, filePath, outPutDir, type);
      } else {
        log.error("do convert fail!exit value:{}, output: {}", result.getExitValue(), result.outputUTF8());
      }
    } catch (IOException e) {
      log.error("do convert happened IOException!", e);
    } catch (InterruptedException e) {
      log.error("do convert happened InterruptedException!", e);
    } catch (TimeoutException e) {
      log.error("do convert happened TimeoutException!filePath:{},page:{}", filePath, page, e);
    } catch (ExecutionException e) {
      log.error("happened!ExecutionException", e);
    } finally {
      if (pdf2htmlProcess != null) {
        SystemProcess process = Processes.newStandardProcess(pdf2htmlProcess);
        try {
          ProcessUtil.destroyGracefullyOrForcefullyAndWait(process, 30, TimeUnit.SECONDS, 10, TimeUnit.SECONDS);
          log.info("pdf2htmlProcess has been killed!");
        } catch (InterruptedException e) {
          log.error("kill pdf2htmlProcess happened InterruptedException!", e);
        } catch (TimeoutException e) {
          log.error("kill pdf2htmlProcess timeout!", e);
        }
      }
      if (type == 1) {
        FileUtils.deleteQuietly(new File(filePath));
      }
    }
    return dataFilePath;
  }

  private List<String> createProcessArgs(String filePath, String outPutDir, int page, int type, int width) {
    if (type == 1) {
      page = 1;
    }
    List<String> args = Lists.newArrayList();
    args.add("pdf2htmlEX");//???????????????
    args.add("-f");
    args.add(String.valueOf(page));
    args.add("-l");
    args.add(String.valueOf(page));
    args.add("--fit-width");//??????px
    args.add(String.valueOf(width));
    args.add("--embed-outline");//????????????????????????
    args.add("0");
    args.add("--embed-css");
    args.add("0");
    if (type == 2) {
      args.add("--css-filename");
      args.add("css" + page + ".css");
    }
    args.add("--process-annotation");
    args.add("1");
    args.add("--embed-image");
    args.add("0");
    args.add("--embed-font");
    args.add("1");
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
    args.add("--embed-javascript");//js??????????????????
    args.add("0");
    args.add("--dest-dir");//????????????
    args.add(outPutDir);
    args.add(filePath);
    log.info("args:{}", args);
    return args;
  }


  private String handleResult(String ea,int page, String filePath, String outPutDir, int type) throws IOException {
    String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
    String dataFileName = FilenameUtils.getBaseName(filePath) + ".html";
    String dataFilePath = FilenameUtils.concat(outPutDir, dataFileName);
    String pageName = page + ".html";
    String pagePath = FilenameUtils.concat(baseDir, pageName);
    File dataFile = new File(dataFilePath);
    if (!dataFile.exists()) {
      return "";
    }
    File pageFile = new File(pagePath);
    String dirName = FilenameUtils.getBaseName(baseDir);
    String cssFileName = type == 1 ? FilenameUtils.getBaseName(filePath) + ".css" : "css" + page + ".css";
    String newCssFileName = page + ".css";
    String cssFileFilePath = FilenameUtils.concat(outPutDir, cssFileName);
    String cssHtml = FileUtils.readFileToString(new File(cssFileFilePath));
    //    //??????????????????
    //    String regex = "url\\(f\\d\\.woff\\)";
    //    Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    //    Matcher matcher = pattern.matcher(cssHtml);
    //    while (matcher.find()) {
    //      String fontStyle = matcher.group();
    //      String fontName = fontStyle.replace("url(", "").replace(")", "");
    //      //????????????
    //      String fontFilePath = FilenameUtils.concat(outPutDir, fontName);
    //      File fontFile = new File(fontFilePath);
    //      if (fontFile.exists()) {
    //        String newFontName = "font_" + page + "_" + fontName;
    //        String newFontFilePath = FilenameUtils.concat(baseDir, newFontName);
    //        File newFontFile = new File(newFontFilePath);
    //        fontFile.renameTo(newFontFile);
    //        String newFontStyle = "url(" + newFontName + ")";
    //        cssHtml = cssHtml.replace(fontStyle, newFontStyle);
    //      }
    //    }
    //????????????
    cssHtml = cssHtml.replace("visibility:hidden", "visibility:visible");
    String newCssFilePath = FilenameUtils.concat(baseDir, newCssFileName);
    FileUtils.writeByteArrayToFile(new File(newCssFilePath), cssHtml.getBytes());
    //??????????????????
    String bgName = "";
    String newBgName = "";
    Path bgPath;
    try (Stream<Path> stream = Files.list(Paths.get(outPutDir)).filter(f -> f.toFile().getName().startsWith("bg"))) {
      bgPath = stream.findFirst().orElse(null);
    }
    if (bgPath != null) {
      File bgFile = bgPath.toFile();
      bgName = bgFile.getName();
      newBgName = "bg" + page + ".jpg";
      String newBgFilePath = FilenameUtils.concat(baseDir, newBgName);
      bgFile.renameTo(new File(newBgFilePath));
      //??????
      try {
        SimpleImageInfo simpleImageInfo = new SimpleImageInfo(new File(newBgFilePath));
        int width = simpleImageInfo.getWidth();
        int height = simpleImageInfo.getHeight();
        if (width > 2000 && height > 2000) {
          int newWidth, newHeight;
          if (width > height) {
            newHeight = 1000;
            newWidth = 1000 * width / height;
          } else {
            newWidth = 1000;
            newHeight = 1000 * height / width;
          }
          log.info("width:{},height:{},newWidth:{},newHeight:{}", width, height, newWidth, newHeight);
          thumbHelper.doThumbnail(FileUtils.readFileToByteArray(new File(newBgFilePath)), newWidth, newHeight, new File(newBgFilePath));
        }
      } catch (Exception ex) {
        log.error("doThumbnail happened error!path:{}", newBgFilePath);
      }

    }
    handleHtml(ea,outPutDir, dataFile, pageFile, dirName, cssFileName, newCssFileName, bgName, newBgName);
    return pagePath;
  }

  private void handleHtml(String ea,
                          String outPutDir,
                          File dataFile,
                          File pageFile,
                          String dirName,
                          String cssName,
                          String newCssName,
                          String bgName,
                          String newBgName) throws IOException {
    try {
      String html = FileUtils.readFileToString(dataFile);
      html = html.replace("<!-- Created by pdf2htmlEX (https://github.com/coolwanglu/pdf2htmlex) -->", "");
      html = html.replace("Evaluation Warning : The document was created with  Spire.Presentation for .NET", "");
      html = html.replace("base.min.css", "../static/css/base.min.css");
      html = html.replace("<link rel=\"stylesheet\" href=\"fancy.min.css\"/>", "");
      html = html.replace(cssName, "./" + dirName + "/" + newCssName + "?" + "ea=" + ea);
      html = html.replace("<script src=\"compatibility.min.js\"></script>", "");
      html = html.replace("<script src=\"pdf2htmlEX.min.js\"></script>", "");
      html = html.replace("<script>\n" + "try{\n" + "pdf2htmlEX.defaultViewer = new pdf2htmlEX.Viewer({});\n" + "}catch(e){}\n" + "</script>", "");
      html = html.replace("<div id=\"sidebar\">\n" + "<div id=\"outline\">\n" + "</div>\n" + "</div>", "");
      html = html.replace("<div class=\"loading-indicator\">", "");
      html = html.replace("<img alt=\"\" src=\"pdf2htmlEX-64x64.png\"/>", "");
      html = html.replace("Evaluation only.", "")
        .replace("Created with Aspose.Slides for .NET 4.0 16.11.0.0.", "")
        .replace("Copyright 2004-2016Aspose Pty Ltd.", "");
      if (!Strings.isNullOrEmpty(bgName)) {
        html = html.replace(bgName, "./" + dirName + "/" + newBgName + "?" + "ea=" + ea);
      }
      html = html.replace("\n", "");
      FileUtils.writeByteArrayToFile(pageFile, html.getBytes());
    } catch (Exception ex) {
      log.error("handleHtml happened exception", ex);
    } finally {
      FileUtils.deleteQuietly(new File(outPutDir));
    }
  }

}
