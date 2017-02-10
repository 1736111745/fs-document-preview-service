package com.facishare.document.preview.api.model.result;

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
public class Pdf2HtmlResult extends ProtoBase {
    @Tag(1)
    private String dataFilePath;
}
