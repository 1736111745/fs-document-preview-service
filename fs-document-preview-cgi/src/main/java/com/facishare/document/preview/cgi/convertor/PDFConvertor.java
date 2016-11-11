package com.facishare.document.preview.cgi.convertor;

import com.fxiaoke.common.image.SimpleImageInfo;
import com.google.common.base.Strings;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;

import java.io.File;


/**
 * Created by liuq on 16/9/9.
 */
public class PDFConvertor implements IDocConvertor {
    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {
        String pngFilePath = ConvertorHelper.toPng(page1, page2, filePath, baseDir, 1, 2);
        if (!Strings.isNullOrEmpty(pngFilePath)) {
            SimpleImageInfo simpleImageInfo=new SimpleImageInfo(new File(pngFilePath));
            if(simpleImageInfo.getWidth()>1024) {
                Thumbnails.of(pngFilePath).width(1024).outputQuality(0.8).toFile(pngFilePath);
            }
            return FilenameUtils.getBaseName(baseDir) + "/" + FilenameUtils.getName(pngFilePath);
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
}
