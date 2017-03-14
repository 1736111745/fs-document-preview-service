package com.facishare.document.preview.common.dao;


import com.facishare.document.preview.common.model.DownloadFileTokens;

/**
 * Created by liuq on 16/8/25.
 */
public interface FileTokenDao {
    DownloadFileTokens getInfo(String ea, String fileToken, String downloadUser);
}
