package com.facishare.document.preview.cgi.dao;

import com.facishare.document.preview.cgi.model.FileToken;

/**
 * Created by liuq on 16/8/25.
 */
public interface FileTokenDao {
    FileToken getInfo(String ea, String fileToken, String downloadUesr );
}
