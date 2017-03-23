package com.facishare.document.preview.api.model.arg;

import com.facishare.common.fsi.ProtoBase;
import com.github.trace.annotation.RpcParameterToString;
import io.protostuff.Tag;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Created by liuq on 2017/3/23.
 */
@Data
@Builder
@RpcParameterToString
@ToString
public class AsycConvertDocArg extends ProtoBase {
    @Tag(1)
    private String ea;
    @Tag(2)
    private String path;
    @Tag(3)
    private int type;  //1.图片 2.html
    @Tag(4)
    private int width;//生产的文件预览宽度
}
