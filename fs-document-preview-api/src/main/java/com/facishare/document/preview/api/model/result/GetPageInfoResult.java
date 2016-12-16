package com.facishare.document.preview.api.model.result;

import com.facishare.common.fsi.ProtoBase;
import com.github.trace.annotation.RpcParameterToString;
import io.protostuff.Tag;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by liuq on 2016/12/14.
 */
@Data
@Builder
@RpcParameterToString
public class GetPageInfoResult extends ProtoBase {
    @Tag(1)
    private boolean success;
    @Tag(1)
    private String errorMsg;
    @Tag(1)
    private int pageCount;
    @Tag(1)
    private List<String> sheetNames;
}
