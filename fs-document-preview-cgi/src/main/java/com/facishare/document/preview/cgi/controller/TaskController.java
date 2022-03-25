package com.facishare.document.preview.cgi.controller;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.cgi.utils.FileUtil;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.github.autoconf.ConfigFactory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.openxml4j.opc.internal.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author liuquan
 * @date 2022/3/24  6:00 下午
 */
@Slf4j
@Controller
@RequestMapping("/task")
public class TaskController {
  @Autowired
  private PreviewInfoDao previewInfoDao;
  private int allowGcDay = 30;

  @PostConstruct
  public void init() {
    ConfigFactory.getConfig("fs-dps-config", config -> {
      allowGcDay = config.getInt("allow.gc.day");
    });
  }

  @RequestMapping("/gc")
  public void gc() {
    int limit = 200;
    int skip = 0;
    int size = 0;
    do {
      List<PreviewInfo> infos = previewInfoDao.getPreviewInfoByPage(skip, limit);
      size = infos.size();
      List<PreviewInfo> canDeleteInfos = infos.stream().filter(i -> canDelete(i.getCreateTime().getTime())).collect(Collectors.toList());
      if(CollectionUtils.isNotEmpty(canDeleteInfos)){
        //gc smb
        canDeleteInfos.forEach(info -> FileUtil.delete(info.getDataDir()));
        //gc meta元数据
        previewInfoDao.clean(canDeleteInfos.stream().map(i -> i.getPath()).collect(Collectors.toList()));
      }
      skip = skip + infos.size();
    } while (size == limit);
  }

  private boolean canDelete(long stamp) {
    long allowGcMill = allowGcDay * 24 * 3600 * 1000;
    return System.currentTimeMillis() - stamp > allowGcMill;
  }
}
