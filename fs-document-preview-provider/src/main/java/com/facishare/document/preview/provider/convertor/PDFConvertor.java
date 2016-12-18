package com.facishare.document.preview.provider.convertor;

/**
 * Created by liuq on 16/9/9.
 */
public class PDFConvertor implements IDocConvertor {
    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {
        return ConvertHelper.toPng(page1, page2, filePath, baseDir, 1, 2);
    }
}
