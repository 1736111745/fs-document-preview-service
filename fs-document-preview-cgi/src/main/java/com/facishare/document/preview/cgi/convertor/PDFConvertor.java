package com.facishare.document.preview.cgi.convertor;

import com.fxiaoke.common.image.SimpleImageInfo;
import com.google.common.base.Strings;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Created by liuq on 16/9/9.
 */
public class PDFConvertor implements IDocConvertor {
    private static final Logger logger = LoggerFactory.getLogger(DocConvertor.class);
    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {

        String pngFilePath = ConvertorHelper.toPng(page1, page2, filePath, baseDir, 1, 2);
        if (!Strings.isNullOrEmpty(pngFilePath)) {
            String jpgFileName = FilenameUtils.getBaseName(pngFilePath) + ".jpg";
            String jpgFilePath = FilenameUtils.concat(baseDir, jpgFileName);
            SimpleImageInfo pngImageInfo = new SimpleImageInfo(new File(pngFilePath));
            if (pngImageInfo.getWidth() > 1024) {
                Thumbnails.of(pngFilePath).width(1024).outputQuality(0.8).toFile(jpgFilePath);
            } else {
                Thumbnails.of(pngFilePath).outputQuality(0.8).toFile(jpgFilePath);
            }
            SimpleImageInfo jpgImageInfo = new SimpleImageInfo(new File(jpgFilePath));
            logger.info("png image width:{},height:{},jpg image width:{},height:{}", pngImageInfo.getWidth(), pngImageInfo.getHeight(), jpgImageInfo.getWidth(), jpgImageInfo.getHeight());
            FileUtils.deleteQuietly(new File(pngFilePath));
            return FilenameUtils.getBaseName(baseDir) + "/" + FilenameUtils.getName(jpgFilePath);
        }
        return pngFilePath;
    }

    @Override
    public String convert(int page1, int page2, String filePath, String baseDir, int width) throws Exception {
        String pngFilePath = ConvertorHelper.toPng(page1, page2, filePath, baseDir, 1, 2);
        if (!Strings.isNullOrEmpty(pngFilePath)) {
            Thumbnails.of(pngFilePath).width(width).outputFormat("png").toFile(pngFilePath);
            return FilenameUtils.getBaseName(baseDir) + "/" + FilenameUtils.getName(pngFilePath);
        }
        return pngFilePath;
    }

//    public static void main(String[] args) throws IOException {
//        String filePath = "E:/Temp/330.jpg";
//        String out1 = "E:/Temp/01.png";
//        String out2 = "E:/Temp/02.jpg";
//        Thumbnails.of(filePath).width(1024).outputQuality(0.8).toFile(out1);
//        Thumbnails.of(filePath).width(1024).outputQuality(0.8).toFile(out2);
//    }

}
