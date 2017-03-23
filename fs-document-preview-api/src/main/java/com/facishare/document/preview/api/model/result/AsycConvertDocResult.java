package com.facishare.document.preview.api.model.result;

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
public class AsycConvertDocResult extends ProtoBase {
    @Tag(1)
    private boolean success;
}
