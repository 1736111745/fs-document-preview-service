package com.facishare.document.preview.provider.convertor;

import com.google.common.base.Strings;
import org.apache.commons.io.FilenameUtils;
import org.aspectj.apache.bcel.generic.RET;


/**
 * Created by liuq on 16/9/9.
 */
public class PDFConvertor implements IDocConvertor {
    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {
        return ConvertorHelper.toPng(page1, page2, filePath, baseDir, 1, 2);
    }
}