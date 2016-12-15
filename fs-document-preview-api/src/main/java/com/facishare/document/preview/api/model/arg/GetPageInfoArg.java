package com.facishare.document.preview.api.model.arg;

import com.facishare.common.fsi.ProtoBase;
import io.protostuff.Tag;
import lombok.Builder;
import lombok.Data;

/**
 * Created by liuq on 2016/12/14.
 */
@Data
@Builder
public class GetPageInfoArg extends ProtoBase {
    @Tag(1)
    private String filePath;
}
