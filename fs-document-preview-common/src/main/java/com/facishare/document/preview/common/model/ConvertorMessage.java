package com.facishare.document.preview.common.model;

import com.facishare.common.fsi.ProtoBase;
import io.protostuff.Tag;
import lombok.Builder;
import lombok.Data;

/**
 * Created by liuq on 2017/3/9.
 */
@Data
@Builder
public class ConvertorMessage extends ProtoBase {
    @Tag(1)
    private String ea;
    @Tag(2)
    private String npath;
    @Tag(3)
    private String filePath;
    @Tag(4)
    private int page;
    @Tag(5)
    private int type;
}
