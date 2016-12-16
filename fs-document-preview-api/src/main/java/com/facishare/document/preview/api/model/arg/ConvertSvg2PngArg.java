package com.facishare.document.preview.api.model.arg;

import com.facishare.common.fsi.ProtoBase;
import com.github.trace.annotation.RpcParameterToString;
import io.protostuff.Tag;
import lombok.Builder;
import lombok.Data;

/**
 * Created by liuq on 2016/12/15.
 */
@Data
@Builder
@RpcParameterToString
public class ConvertSvg2PngArg extends ProtoBase {
    @Tag(1)
    private String svgFilePath;
    @Tag(2)
    private String pngFilePath;
}
