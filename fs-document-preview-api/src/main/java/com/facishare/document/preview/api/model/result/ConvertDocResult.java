package com.facishare.document.preview.api.model.result;

import com.facishare.common.fsi.ProtoBase;
import io.protostuff.Tag;
import lombok.Builder;
import lombok.Data;

/**
 * Created by liuq on 2016/12/14.
 */
@Data
@Builder
public class ConvertDocResult extends ProtoBase {
    @Tag(1)
    private String dataFilePath;
}
