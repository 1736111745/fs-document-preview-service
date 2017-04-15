package com.facishare.document.preview.common.model;

import lombok.Data;

/**
 * Created by liuq on 2017/4/14.
 */
@Data
public class RestResponse {
    private byte[] bytes;
    private String contentType;
}
