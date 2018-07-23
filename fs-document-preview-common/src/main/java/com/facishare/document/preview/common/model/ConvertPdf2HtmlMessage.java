package com.facishare.document.preview.common.model;

import io.protostuff.Tag;
import lombok.Data;

/**
 * Created by liuq on 2017/4/16.
 */
@Data
public class ConvertPdf2HtmlMessage extends ConvertMessageBase{
    @Tag(5)
    private String filePath;//物理文件名
    @Tag(6)
    private int page;//页码
    @Tag(7)
    private int type;//1:单页pdf  2:多页pdf
    @Tag(8)
    private int pdfConvertType;//0:html 1:png
}
