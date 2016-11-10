package com.facishare.document.preview.cgi.dao;

import com.facishare.document.preview.cgi.model.DataFileInfo;
import com.facishare.document.preview.cgi.model.DocPreviewInfo;

import java.io.IOException;
import java.util.List;

/**
 * Created by liuq on 16/8/16.
 */
public interface DocPreviewInfoDao {

    void saveDocPreviewInfo(String ea,String path,String dataFilePath,List<String> filePathList) throws IOException;

    DataFileInfo getDataFileInfo( String ea,String path, int page,DocPreviewInfo previewInfo) throws IOException;

    String getBaseDir(String folderName);

    DocPreviewInfo initDocPreviewInfo( String ea, int employeeId,String path, String originalFilePath, String dataDir, long docSize, int pageCount, List<String> sheetNames);

    DocPreviewInfo getInfoByPath(String ea, String path);


}
