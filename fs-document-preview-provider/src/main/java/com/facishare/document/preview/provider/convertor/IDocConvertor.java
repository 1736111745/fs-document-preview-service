package com.facishare.document.preview.provider.convertor;

/**
 * Created by liuq on 16/9/9.
 */
/*
文档转换接口：svg,jpg,png,html
 */
public interface IDocConvertor {
    String convert2Svg(String filePath,int startPageIndex, int endPageIndex) throws Exception;

    String convert2Png(String filePath,int startPageIndex, int endPageIndex) throws Exception;

    String convert2Jpg(String filePath,int startPageIndex, int endPageIndex) throws Exception;

    String convert2Html(String filePath,int pageIndex) throws Exception;
}

