package com.facishare.document.preview.common.dao;


import com.facishare.document.preview.common.model.PreviewInfo;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by liuq on 16/8/16.
 */
public interface PreviewInfoDao {

  void savePreviewInfo(String ea, String path, String dataFilePath, int width);

  String getDataFilePath(String path, int page, String dataDir, String filePath, int type, List<String> filePathList) throws IOException;

  String getBaseDir(String folderName);

  PreviewInfo initPreviewInfo(String ea, int employeeId, String path, String originalFilePath, String dataDir, long docSize, int pageCount,
                              List<String> sheetNames, int width, int pdfConvertType);

  PreviewInfo getInfoByPath(String ea, String path, int width);

  //批量删除预览文档
  void patchClean(String ea, List<String> pathList);

  void patchClean(String ea);

  void clean(List<String> pathList);

  //查询预览文档
  List<PreviewInfo> getInfoByPathList(String ea, List<String> pathList);

  List<PreviewInfo> getPreviewInfoByPage(int limit, Date maxDate);
}
