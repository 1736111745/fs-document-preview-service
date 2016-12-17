package com.facishare.document.preview.api.model.arg;

import com.facishare.common.fsi.ProtoBase;
import com.github.trace.annotation.RpcParameterToString;
import io.protostuff.Tag;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Created by liuq on 2016/12/14.
 */
@Data
@Builder
@RpcParameterToString
@ToString
public class ConvertDocArg extends ProtoBase {
    @Tag(1)
    private String path;
    @Tag(2)
    private String originalFilePath;
    @Tag(3)
    int page;
}
