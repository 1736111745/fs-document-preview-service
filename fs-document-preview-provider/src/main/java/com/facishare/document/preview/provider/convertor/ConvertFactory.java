package com.facishare.document.preview.provider.convertor;

import application.dcs.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 创建Convert对象
 * Created by lirui on 2016-12-18 21:34.
 */
@Slf4j
public class ConvertFactory implements PooledObjectFactory<Convert> {
  private static String CONFIG_DIR = getConfigDir();

  private static String getConfigDir() {
    String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    String configDir;
    String profile = System.getProperty("spring.profiles.active");
    if (profile == null || profile.length() == 0) {
      profile = System.getProperty("process.profile");
    }
    if (profile != null) {
      if (!profile.equals("foneshare")) {
        configDir = root + "localhost";
      } else {
        InetAddress ia;
        try {
          ia = InetAddress.getLocalHost();
          String host = ia.getHostName();
          configDir = root + host;
        } catch (UnknownHostException e) {
          configDir = root + "localhost";
        }
        log.info("configDir:{}", configDir);
      }
    } else {
      configDir = root + "localhost";
    }
    return configDir;
  }

  @Override
  public PooledObject<Convert> makeObject() throws Exception {
    Convert convert = new Convert(CONFIG_DIR);
    //convert.setTempPath(new PathHelper().getConvertTempPath());
    convert.setAutoDeleteTempFiles(true);
    convert.setHtmlTitle("文档预览");
    convert.setShowTitle(true);
    convert.setShowPic(true);
    convert.setXlsxMaxRowCol(1000, 100); // 最多转换1000行100列
    convert.setEmptyCount(100, 200);
    convert.setHtmlEncoding("UTF-8");
    convert.setConvertForPhone(true);
    convert.setTimeout(20);
    return new DefaultPooledObject<>(convert);
  }

  @Override
  public void destroyObject(PooledObject<Convert> p) throws Exception {
    p.getObject().close();
    System.gc();
  }

  @Override
  public boolean validateObject(PooledObject<Convert> p) {
    return true;
  }

  @Override
  public void activateObject(PooledObject<Convert> p) throws Exception {
  }

  @Override
  public void passivateObject(PooledObject<Convert> p) throws Exception {

  }
}
