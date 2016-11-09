package com.facishare.document.preview.cgi.convertor;

import com.google.common.base.Strings;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;

/**
 * Created by liuq on 16/9/9.
 */
public class WordConvertor implements IDocConvertor {

    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {
        String svgFilePath = ConvertorHelper.toSvg(page1, page2, filePath, baseDir);
        return Strings.isNullOrEmpty(svgFilePath) ? svgFilePath : FilenameUtils.getBaseName(baseDir) + "/" + FilenameUtils.getName(svgFilePath);
    }

    @Override
    public String convert(int page1, int page2, String filePath, String baseDir, int width) throws Exception {
        String pngFilePath = ConvertorHelper.toPng(page1, page2, filePath, baseDir,1,1);
        if(!Strings.isNullOrEmpty(pngFilePath)) {
            Thumbnails.of(pngFilePath).width(width).outputFormat("png").toFile(pngFilePath);
            return FilenameUtils.getBaseName(baseDir) + "/" + FilenameUtils.getName(pngFilePath);
        }
        return pngFilePath;
    }
}