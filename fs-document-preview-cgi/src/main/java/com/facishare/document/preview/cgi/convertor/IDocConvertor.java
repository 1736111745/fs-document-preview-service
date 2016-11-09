package com.facishare.document.preview.cgi.convertor;

/**
 * Created by liuq on 16/9/9.
 */
public interface IDocConvertor {
    String convert(int page1, int page2, String filePath, String folder) throws Exception;

    String convert(int page1, int page2, String filePath, String folder, int width) throws Exception;
}

