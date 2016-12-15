package com.facishare.document.preview.provider.convertor;

import com.google.common.base.Strings;
import org.apache.commons.io.FilenameUtils;

/**
 * Created by liuq on 16/9/9.
 */
public class PPTConvertor implements IDocConvertor {
    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {
        String svgFilePath = ConvertorHelper.toSvg(page1, page2, filePath, baseDir);
        return Strings.isNullOrEmpty(svgFilePath) ? svgFilePath : FilenameUtils.getBaseName(baseDir) + "/" + FilenameUtils.getName(svgFilePath);
    }
}