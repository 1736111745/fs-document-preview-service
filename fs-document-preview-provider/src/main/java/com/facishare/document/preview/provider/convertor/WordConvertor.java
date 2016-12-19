package com.facishare.document.preview.provider.convertor;

/**
 * Created by liuq on 16/9/9.
 */
public class WordConvertor implements IDocConvertor {

    @Override
    public String convert(int page1, int page2, String filePath, String baseDir) throws Exception {
        return ConvertorHelper.toSvg(page1, page2, filePath, baseDir);
    }

}