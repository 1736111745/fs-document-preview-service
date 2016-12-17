package com.facishare.document.preview.cgi.model;

import lombok.Builder;
import lombok.Data;

/**
 * Created by liuq on 16/9/6.
 */
@Data
@Builder
public class DataFileInfo {
    private String filePath;
    private String originalFilePath;
}
