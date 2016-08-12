package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.convertor.ConvertorHelper;
import com.facishare.document.preview.cgi.utils.SampleUUID;
import com.github.autoconf.spring.reloadable.ReloadableProperty;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by liuq on 16/8/5.
 */
@Controller
@RequestMapping("/dps")
public class PreviewController {
    private static final Logger LOG = LoggerFactory.getLogger(PreviewController.class);

    @ReloadableProperty("data-dir")
    private String dataDir="";

    @RequestMapping(value = "/upload",method= RequestMethod.POST,produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String doUpload(@RequestParam("file") MultipartFile file) throws ServletException, IOException {
        if (!file.isEmpty()) {
            String resultDir = dataDir + "/Result/";
            String fileName = file.getOriginalFilename();
            String filePath = dataDir + "/Sample/" + fileName;
            FileUtils.writeByteArrayToFile(new File(filePath), file.getBytes());
            String resultFileName = SampleUUID.getUUID() + ".html";
            String resultFilePath = resultDir + resultFileName;
            int code = ConvertorHelper.doConvert(filePath, resultFilePath);
            LOG.info("code:{}", code);
            return resultFileName;
        }
        return "error";
    }
    @RequestMapping("/preview/{filePath:.+}")
    public void doPreview(@PathVariable String filePath,HttpServletResponse response) throws IOException {
        String resultDir = dataDir + "/Result/";
        String resultFilePath = resultDir + filePath;
        outPut(response, resultFilePath);
    }

    //@RequestMapping("preview/{type}/{folder}.files/{filename:.+}")
    @RequestMapping("/preview/{folder}.files/{filename:.+}")
    public void getStatic(@PathVariable String folder, @PathVariable String filename, HttpServletResponse response) throws IOException {
        folder=folder+".files";
        String resultDir = dataDir + "/Result/";
        String filePath = resultDir + "/" + folder + "/" + filename;
        if (filename.contains(".png")) {
            response.setContentType("image/png");
        } else if (filename.contains(".js")) {
            response.setContentType("application/javascript");
        } else if (filename.contains(".css")) {
            response.setContentType("text/css");
        }
        outPut(response,filePath);
    }

    private void outPut(HttpServletResponse response,String filePath) throws IOException {
        FileChannel fc = new RandomAccessFile(filePath, "r").getChannel();
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        byte[] buffer = new byte[(int) fc.size()];
        mbb.get(buffer);
        OutputStream out = response.getOutputStream();
        out.write(buffer);
        out.flush();
        out.close();
        mbb.force();
        fc.close();
    }

    private static String getHtml(File file) throws IOException {
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
}

