package com.facishare.document.preview.provider.convertor;

/**
 * Created by liuq on 16/9/9.
 */
public class WordConvertor implements IDocConvertor {

    @Override
    public String convert2Svg(String filePath, int startPageIndex, int endPageIndex) throws Exception {
        return ConvertorHelper.toSvg(filePath,startPageIndex,endPageIndex,1);
    }

    @Override
    public String convert2Png(String filePath, int startPageIndex, int endPageIndex) throws Exception {
        return ConvertorHelper.toPng(filePath,startPageIndex,endPageIndex,1);
    }

    @Override
    public String convert2Jpg(String filePath, int startPageIndex, int endPageIndex) throws Exception {
        return ConvertorHelper.toJpg(filePath,startPageIndex,endPageIndex,1);
    }

    @Override
    public String convert2Html(String filePath, int pageIndex) throws Exception {
        return null;
    }

}