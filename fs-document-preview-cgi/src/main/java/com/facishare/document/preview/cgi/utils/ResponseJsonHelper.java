package com.facishare.document.preview.cgi.utils;

import com.alibaba.fastjson.JSONObject;
import com.facishare.document.preview.common.model.PreviewJsonInfo;
import com.facishare.document.preview.common.utils.DocPreviewInfoHelper;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuq on 2017/5/26.
 */
@UtilityClass
public class ResponseJsonHelper {
  public String getPreviewInfoResult(int pageCount, List<String> sheetNames, String path, String securityGroup) {
    Map<String, Object> map = new HashMap<>();
    map.put("canPreview", true);
    map.put("pageCount", pageCount);
    map.put("path", path);
    map.put("sg", securityGroup);
    map.put("sheets", sheetNames);
    return JSONObject.toJSONString(map);
  }

  public String getPreviewInfoResult(String errorMsg) {
    Map<String, Object> map = new HashMap<>();
    map.put("canPreview", false);
    map.put("errorMsg", errorMsg);
    return JSONObject.toJSONString(map);
  }

  public String getDocPreviewInfoResult(String path, int pageCount) throws Exception {
    PreviewJsonInfo docPreviewInfo = DocPreviewInfoHelper.getPreviewInfo(path);
    docPreviewInfo.setPageCount(pageCount);
    return JSONObject.toJSONString(docPreviewInfo);
  }
}
