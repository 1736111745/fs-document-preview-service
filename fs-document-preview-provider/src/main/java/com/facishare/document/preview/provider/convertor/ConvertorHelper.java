package com.facishare.document.preview.provider.convertor;

import application.dcs.Convert;
import application.dcs.IHtmlConvertor;
import application.dcs.IPICConvertor;
import com.facishare.document.preview.provider.utils.FilePathHelper;
import com.github.autoconf.ConfigFactory;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.concurrent.*;


/**
 * Created by liuq on 2016/11/9.
 */
@Slf4j
@UtilityClass
public class ConvertorHelper {
  private GenericObjectPool<Convert> pool;
  private final ThreadFactory factory =
    new ThreadFactoryBuilder().setDaemon(true).setNameFormat("convertor-%d").build();
  private final ExecutorService executorService = Executors.newCachedThreadPool(factory);


  private interface IConvertJob<V> {
    V doConvert(Convert convert) throws Exception;
  }

  static {
    ConfigFactory.getConfig("fs-dps-config", conf -> {
      GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
      poolConfig.setMaxTotal(300);
      poolConfig.setMaxIdle(100);
      poolConfig.setMinIdle(30);
      poolConfig.setTestOnBorrow(false);
      poolConfig.setTestOnCreate(false);
      poolConfig.setTestWhileIdle(false);
      poolConfig.setJmxEnabled(true);
      poolConfig.setMaxWaitMillis(20000);
      if (conf.getBool("removeIdleConvertor", true)) {
        poolConfig.setSoftMinEvictableIdleTimeMillis(600000); //空闲超过30分钟则回收对象
        poolConfig.setTimeBetweenEvictionRunsMillis(60000); // 1分钟检测1次空闲对象
      }
      GenericObjectPool<Convert> old = null;
      if (pool != null) {
        old = pool;
      }
      pool = new GenericObjectPool<>(new ConvertFactory(), poolConfig);
      if (old != null) {
        old.clear();
        old.close();
      }
    });
  }

  /**
   * 异步执行任务，最多等待30秒，否则主动停止任务
   *
   * @param callable
   * @param <V>
   * @return
   * @throws Exception
   */
  private <V> V asyncExec(Callable<V> callable) throws Exception {
    log.info("begin async exec~");
    Future<V> future = executorService.submit(callable);
    try {
      return future.get(30, TimeUnit.SECONDS);
    } finally {
      if (!future.isDone()) {
        future.cancel(true);
      }
    }
  }

  /**
   * 把从pool借对象以及在另外一个线程执行任务的逻辑封装起来
   *
   * @param job
   * @param <V>
   * @return
   */
  private <V> V doConvert(IConvertJob<V> job, String args) {
    Stopwatch sw = Stopwatch.createStarted();
    Convert convert;
    try {
      convert = pool.borrowObject();
    } catch (Exception e) {
      log.error("cannot borrow convertor", e);
      return null;
    }
    V val = null;
    try {
      val = asyncExec(() -> job.doConvert(convert));
      pool.returnObject(convert);
      return val;
    } catch (Exception e) {
      log.error("cannot convert, args: {}", args, e);
      try {
        pool.invalidateObject(convert);
      } catch (Exception e1) {
        log.error("cannot invalid convertor", e1);
      }
    }
    log.info("convert cost: {} ms, args: {}", sw.stop().elapsed(TimeUnit.MILLISECONDS), args);
    return val;
  }

  public String toSvg(String filePath, int startPageIndex, int endPageIndex, int startIndex) throws Exception {
    String args =
      String.format("filePath:%s,startPageIndex:%s,endPageIndex:%s,startIndex:%s", filePath, startPageIndex, endPageIndex, startIndex);
    log.info("start convert doc to svg,args:{}", args);
    String svgFileExt = "svg";
    String resultFilePath = "";
    IPICConvertor picConvertor = doConvert(convert -> convert.convertMStoPic(filePath), args);
    if (picConvertor != null) {
      int resultCode = picConvertor.resultCode();
      if (resultCode == 0) {
        String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        int retCode;
        try {
          retCode = asyncExec(() -> picConvertor.convertToSVG(startPageIndex, endPageIndex, 1.0f, baseDir));
        } finally {
          picConvertor.close();
        }
        if (retCode == 0) {
          String svgFilePath = FilePathHelper.getFilePath(filePath, startPageIndex, startIndex, svgFileExt);
          if (FileUtils.getFile(svgFilePath).exists()) {
            resultFilePath = svgFilePath;
          } else {
            log.warn("convert2Svg completed,but aim file does't create,args:{},aim file:{}", args, svgFilePath);
          }
        } else {
          log.warn("convert2Svg completed,but ret code is:{}", retCode, args);
        }
      } else {
        log.warn("get picConvertor fail,args:{},resultCode:{}", args, resultCode);
      }
    } else {
      log.warn("picConvertor is null,args:{}", args);
    }
    return resultFilePath;
  }

  public String toJpg(String filePath, int startPageIndex, int endPageIndex, int startIndex) throws Exception {
    String args =
      String.format("filePath:%s,startPageIndex:%s,endPageIndex:%s,startIndex:%s", filePath, startPageIndex, endPageIndex, startIndex);
    log.info("start convert doc to jpg,args:{}", args);
    String jpgFileExt = "jpg";
    String resultFilePath = "";
    String fileExt = FilenameUtils.getExtension(filePath).toLowerCase();
    IPICConvertor picConvertor = fileExt.equals("pdf") ?
      doConvert(convert -> convert.convertPdftoPic(filePath), args) :
      doConvert(convert -> convert.convertMStoPic(filePath), args);
    if (picConvertor != null) {
      int resultCode = picConvertor.resultCode();
      if (resultCode == 0) {
        String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        try {
          asyncExec(() -> picConvertor.convertToJPG(startPageIndex, endPageIndex, 2f, baseDir));
        } finally {
          picConvertor.close();
        }
        String jpgFilePath = FilePathHelper.getFilePath(filePath, startPageIndex, startIndex, jpgFileExt);
        if (FileUtils.getFile(jpgFilePath).exists()) {
          resultFilePath = jpgFilePath;
        } else {
          log.warn("convert2Jpg completed,bug aim file does't create,args:{},aim file:{}", args, jpgFilePath);
        }
      } else {
        log.warn("get picConvertor fail,args:{},resultCode:{}", args, resultCode);
      }
    } else {
      log.warn("picConvertor is null,args:{}", args);
    }
    return resultFilePath;
  }

  public String toPng(String filePath, int startPageIndex, int endPageIndex, int startIndex) throws Exception {
    String args =
      String.format("filePath:%s,startPageIndex:%s,endPageIndex:%s,startIndex:%s", filePath, startPageIndex, endPageIndex, startIndex);
    log.info("start convert doc to png,args:{}", args);
    String pngFileExt = "png";
    String resultFilePath = "";
    String fileExt = FilenameUtils.getExtension(filePath).toLowerCase();
    IPICConvertor picConvertor = fileExt.equals("pdf") ?
      doConvert(convert -> convert.convertPdftoPic(filePath), args) :
      doConvert(convert -> convert.convertMStoPic(filePath), args);
    if (picConvertor != null) {
      // picConvertor.getPageCount();
      int resultCode = picConvertor.resultCode();
      if (resultCode == 0) {
        String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        try {
          asyncExec(() -> picConvertor.convertToPNG(startPageIndex, endPageIndex, 2f, baseDir));
        } finally {
          picConvertor.close();
        }
        String pngFilePath = FilePathHelper.getFilePath(filePath, startPageIndex, startIndex, pngFileExt);
        if (FileUtils.getFile(pngFilePath).exists()) {
          resultFilePath = pngFilePath;
        } else {
          log.warn("convert2Png completed,bug aim file does't create,args:{},aim file:{}", args, pngFilePath);
        }
      } else {
        log.warn("get picConvertor fail,args:{},resultCode:{}", args, resultCode);
      }
    } else {
      log.warn("picConvertor is null,args:{}", args);
    }
    return resultFilePath;
  }

  public String toHtml(String filePath, int pageIndex, int startIndex) throws Exception {
    String args = String.format("filePath:%s,pageIndex:%s,startIndex:%s", filePath, pageIndex, startIndex);
    log.info("start convert doc to html,args:{}", args);
    String htmlFileExt = "html";
    String resultFilePath = "";
    IHtmlConvertor htmlConvertor = doConvert(convert -> convert.convertMStoHtml(filePath), args);
    if (htmlConvertor != null) {
      int resultCode = htmlConvertor.resultCode();
      if (resultCode == 0) {
        htmlConvertor.setNormal(true);
        String htmlFilePath = FilePathHelper.getFilePath(filePath, pageIndex, startIndex, htmlFileExt);
        try {
          asyncExec(() -> {
            htmlConvertor.convertToHtml(htmlFilePath, pageIndex);
            return null;
          });
        } finally {
          htmlConvertor.close();
        }
        if (FileUtils.getFile(htmlFilePath).exists()) {
          resultFilePath = htmlFilePath;
        } else {
          log.warn("convert2Html completed,bug aim file does't create,args:{},aim file:{}", args, resultFilePath);
        }
      } else {
        log.warn("get htmlConvertor fail,args:{},resultCode:{}", args, resultCode);
      }
    } else {
      log.warn("htmlConvertor is null,args:{}", args);
    }
    return resultFilePath;
  }

  public int getOldWordOrPPTPageCount(String filePath) throws Exception {
    int pageCount = 0;
    IPICConvertor ipicConvertor = doConvert(convert -> convert.convertMStoPic(filePath), filePath);
    if (ipicConvertor != null) {
      try {
        pageCount = ipicConvertor.getPageCount();
      } finally {
        ipicConvertor.close();
      }
    }
    return pageCount;
  }
}
