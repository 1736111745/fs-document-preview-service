package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.common.model.PreviewInfo;
import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * @author liuquan
 * @date 2022/1/6  11:32 上午
 */
@UtilityClass
public class FileUtil {

  public boolean exists(PreviewInfo previewInfo) {
    return new File(previewInfo.getDataDir()).exists();
  }
}
