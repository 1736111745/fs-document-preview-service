package com.facishare.document.preview.provider.convertor;

import application.dcs.Convert;
import application.dcs.IHtmlConvertor;
import application.dcs.IPICConvertor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.File;

/**
 * 使用pool创建convertor
 * Created by lirui on 2016-12-18 21:02.
 */
@Slf4j
public class ConvertHelper {
  private ConvertHelper() {
  }

  private static GenericObjectPool<Convert> pool;

  static {
    GenericObjectPoolConfig config = new GenericObjectPoolConfig();
    config.setMaxTotal(300);
    config.setMaxIdle(50);
    config.setMinIdle(10);
    config.setTestOnBorrow(false);
    config.setTestOnCreate(false);
    config.setTestWhileIdle(false);
    config.setJmxEnabled(false);
    config.setMaxWaitMillis(200000);
    pool = new GenericObjectPool<>(new ConvertFactory(), config);
  }


  public static String toSvg(int page1, int page2, String filePath, String baseDir) {
    Convert convert = null;
    try {
      convert = pool.borrowObject();
      IPICConvertor ipicConvertor = convert.convertMStoPic(filePath);
      if (ipicConvertor != null) {
        int resultCode = ipicConvertor.resultCode();
        if (resultCode == 0) {
          String fileName = (page1 + 1) + ".svg";
          String svgFilePath = FilenameUtils.concat(baseDir, fileName);
          ipicConvertor.convertToSVG(page1, page2, 1.0f, baseDir);
          ipicConvertor.close();
          File file = new File(svgFilePath);
          if (file.exists()) {
            return svgFilePath;
          } else {
            return "";
          }
        } else {
          log.warn("filePath:{},resultCode:{}", filePath, resultCode);
          return "";
        }
      } else {
        log.warn("converter is null");
        return "";
      }
    } catch (Exception e) {
      log.error("toSvg, filePath:{}", filePath, e);
      return "";
    } finally {
      if (convert != null) {
        pool.returnObject(convert);
      }
    }
  }

  public static String toPng(int page1, int page2, String filePath, String baseDir, int startIndex, int type) {
    Convert convert = null;
    try {
      convert = pool.borrowObject();
      IPICConvertor ipicConvertor = type == 1 ? convert.convertMStoPic(filePath) : convert.convertPdftoPic(filePath);
      int resultCode = ipicConvertor.resultCode();
      if (resultCode == 0) {
        String fileName = (page1 + startIndex) + ".png";
        String pngFilePath = baseDir + "/" + fileName;
        ipicConvertor.convertToPNG(page1, page2, 2f, baseDir);
        ipicConvertor.close();
        File file = new File(pngFilePath);
        if (file.exists()) {
          return pngFilePath;
        } else {
          return "";
        }
      } else {
        log.warn("filePath:{},pageIndex:{},resultCode:{}", filePath, page1, resultCode);
        return "";
      }
    } catch (Exception e) {
      log.error("toPng,filePath:{}", filePath, e);
      return "";
    } finally {
      if (convert != null) {
        pool.returnObject(convert);
      }
    }
  }

  public static String toHtml(int page1, String filePath, String baseDir) {
    Convert convert = null;
    try {
      convert = pool.borrowObject();
      IHtmlConvertor htmlConvertor = convert.convertMStoHtml(filePath);
      int resultCode = htmlConvertor.resultCode();
      if (resultCode == 0) {
        htmlConvertor.setNormal(true);
        String fileName = (page1 + 1) + ".html";
        String htmlFilePath = baseDir + "/" + fileName;
        htmlConvertor.convertToHtml(htmlFilePath, page1);
        htmlConvertor.close();
        File file = new File(htmlFilePath);
        if (file.exists()) {
          return htmlFilePath;
        } else {
          return "";
        }
      } else {
        log.warn("resultCode:{}", resultCode);
        return "";
      }
    } catch (Exception e) {
      log.error("toHtml,filePath:{}", filePath, e);
      return "";
    } finally {
      if (convert != null) {
        pool.returnObject(convert);
      }
    }
  }

}
