package com.facishare.document.preview.provider.model;

import lombok.Builder;
import lombok.Data;

/**
 * Created by liuq on 2017/1/7.
 */
@Data
@Builder
public class SheetInfo {
    private String sheetName;
    private boolean isHidden;
    private boolean isActvie;
}
