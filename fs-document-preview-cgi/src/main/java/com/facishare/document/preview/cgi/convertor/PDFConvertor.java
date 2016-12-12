package com.facishare.document.preview.cgi.convertor;

import com.facishare.document.preview.cgi.model.ImageSize;
import com.facishare.document.preview.cgi.utils.ThumbnailSizeHelper;
import com.google.common.base.Strings;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;


/**
 * Created by liuq on 16/9/9.
 */
public class PDFConvertor implements IDocConvertor {
    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {
        String pngFilePath = ConvertorHelper.toPng(page1, page2, filePath, baseDir, 1, 2);
        return Strings.isNullOrEmpty(pngFilePath) ? pngFilePath : FilenameUtils.getBaseName(baseDir) + "/" + FilenameUtils.getName(pngFilePath);
    }
}
