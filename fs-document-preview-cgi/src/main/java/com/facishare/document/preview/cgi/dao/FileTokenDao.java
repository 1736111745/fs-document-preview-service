package com.facishare.document.preview.cgi.dao;

import com.facishare.document.preview.cgi.model.DownloadFileTokens;

/**
 * Created by liuq on 16/8/25.
 */
public interface FileTokenDao {
    DownloadFileTokens getInfo(String ea, String fileToken, String downloadUesr );
}
