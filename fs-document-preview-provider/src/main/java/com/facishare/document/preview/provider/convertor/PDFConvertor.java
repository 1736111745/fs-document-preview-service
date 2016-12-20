package com.facishare.document.preview.provider.convertor;

import com.google.common.base.Strings;
import org.apache.commons.io.FilenameUtils;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by liuq on 16/9/9.
 */
@Component
public class PDFConvertor implements IDocConvertor {
    @Autowired
    ConvertorHelper convertorHelper;

    @Override
    public String convert2Svg(String filePath, int startPageIndex, int endPageIndex) throws Exception {
        return null;
    }

    @Override
    public String convert2Png(String filePath, int startPageIndex, int endPageIndex) throws Exception {
        return convertorHelper.toPng(filePath,startPageIndex,endPageIndex,1);
    }

    @Override
    public String convert2Jpg(String filePath, int startPageIndex, int endPageIndex) throws Exception {
        return convertorHelper.toJpg(filePath,startPageIndex,endPageIndex,1);
    }

    @Override
    public String convert2Html(String filePath, int startPageIndex, int endPageIndex) throws Exception {
        return null;
    }
}
