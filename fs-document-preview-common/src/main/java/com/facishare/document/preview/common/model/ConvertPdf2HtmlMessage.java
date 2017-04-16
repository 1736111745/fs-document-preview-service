package com.facishare.document.preview.common.model;

import io.protostuff.Tag;
import lombok.Builder;
import lombok.Data;

/**
 * Created by liuq on 2017/4/16.
 */
@Data
public class ConvertPdf2HtmlMessage extends ConvertMessageBase{
    @Tag(1)
    private String filePath;//物理文件名
    @Tag(2)
    private int page;//页码
    @Tag(3)
    private int type;//1:单页pdf  2:多页pdf
}
