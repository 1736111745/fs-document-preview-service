package com.facishare.document.preview.cgi.dao;

import com.facishare.document.preview.cgi.model.DataFileInfo;
import com.facishare.document.preview.cgi.model.PreviewInfo;

import java.io.IOException;
import java.util.List;

/**
 * Created by liuq on 16/8/16.
 */
public interface PreviewInfoDao {

    void savePreviewInfo(String ea,String path,String dataFilePath) throws IOException;

    DataFileInfo getDataFileInfo( String ea,String path, int page,PreviewInfo previewInfo) throws IOException;

    String getBaseDir(String folderName);

    PreviewInfo initPreviewInfo( String ea, int employeeId,String path, String originalFilePath, String dataDir, long docSize, int pageCount, List<String> sheetNames);

    PreviewInfo getInfoByPath(String ea,String path);

}
