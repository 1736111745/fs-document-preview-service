package com.facishare.document.preview.common.model;

import com.facishare.common.fsi.ProtoBase;
import io.protostuff.Tag;
import lombok.Builder;
import lombok.Data;

/**
 * Created by liuq on 2017/4/7.
 */
@Data
@Builder
public class ConvertOffice2PdfMessage  extends ProtoBase {
    @Tag(1)
    private String ea;
    @Tag(2)
    private int employeeId;
    @Tag(3)
    private String path;
    @Tag(4)
    private String sg;
}
