package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.utils.FileUtil;
import com.facishare.document.preview.common.dao.PreviewInfoDao;
import com.facishare.document.preview.common.model.PreviewInfo;
import com.facishare.document.preview.common.utils.PathHelper;
import com.github.autoconf.ConfigFactory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    int limit = 100;
    int skip = 0;
    int size = 0;
    do {
      List<PreviewInfo> infos = previewInfoDao.getPreviewInfoByPage(limit, new Date(System.currentTimeMillis() - allowGcDay * 24 * 3600 * 1000));
      size = infos.size();
      if (CollectionUtils.isNotEmpty(infos)) {
        //gc smb
        infos.forEach(info -> FileUtil.delete(info.getDataDir()));
        //gc meta元数据
        previewInfoDao.clean(infos.stream().map(i -> i.getPath()).collect(Collectors.toList()));
      }
      skip = skip + infos.size();
    } while (size == limit);
    FileUtil.deleteEmptyDir(new PathHelper().getParentDir());
  }

  @RequestMapping("/gcPath")
  public void gcPath(@RequestParam("path") String path) {
    previewInfoDao.clean(Lists.newArrayList(path));
  }

  @RequestMapping("/gcEnterprise")
  public void gcEnterprise(@RequestParam("ea") String ea) {
    previewInfoDao.patchClean(ea);
  }
}
