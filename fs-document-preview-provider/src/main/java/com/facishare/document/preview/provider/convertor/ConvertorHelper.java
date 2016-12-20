package com.facishare.document.preview.provider.convertor;

import application.dcs.Convert;
import application.dcs.IHtmlConvertor;
import application.dcs.IPICConvertor;
import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.provider.utils.FilePathHelper;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;


/**
 * Created by liuq on 2016/11/9.
 */
@Slf4j
public class ConvertorHelper {
    private static GenericObjectPool<Convert> pool;

    private ConvertorHelper()
    {

    }

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


    public static String toSvg(String filePath, int startPageIndex, int endPageIndex, int startIndex) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String args = String.format("filePath:%s,startPageIndex:%s,endPageIndex:%s,startIndex:%s", filePath, startPageIndex, endPageIndex, startIndex);
        log.info("start convert doc to svg,args:{}", args);
        String svgFileExt = "svg";
        String resultFilePath = "";
        Convert convert = null;
        try {
            convert = pool.borrowObject();
            @Cleanup IPICConvertor picConvertor = convert.convertMStoPic(filePath);
            if (picConvertor != null) {
                int resultCode = picConvertor.resultCode();
                if (resultCode == 0) {
                    String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
                    int retCode=picConvertor.convertToSVG(startPageIndex, endPageIndex, 0.5f, baseDir);
                    if(retCode==0) {
                        String svgFilePath = FilePathHelper.getFilePath(filePath, startPageIndex, startIndex, svgFileExt);
                        if (FileUtils.getFile(svgFilePath).exists()) {
                            resultFilePath = svgFilePath;
                        } else {
                            log.warn("convert2Svg completed,but aim file does't create,args:{},aim file:{}", args, svgFilePath);
                        }
                    }
                    else
                    {
                        log.warn("convert2Svg completed,but ret code is:{}",retCode, args);
                    }
                } else {
                    log.warn("get picConvertor fail,args:{},resultCode:{}", args, resultCode);
                }
            } else {
                log.warn("picConvertor is null,args:{}", args);
            }
        } catch (Exception e) {
            log.error("toSvg happened exception,args:{}", args, e);
        } finally {
            if (convert != null) {
                pool.returnObject(convert);
            }
            stopWatch.stop();
            log.info("toSvg finished,args:{},cost:{}", args, stopWatch.getTime() + "ms");
            return resultFilePath;
        }
    }

    public static String toJpg(String filePath, int startPageIndex, int endPageIndex, int startIndex) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String args = String.format("filePath:%s,startPageIndex:%s,endPageIndex:%s,startIndex:%s", filePath, startPageIndex, endPageIndex, startIndex);
        log.info("start convert doc to jpg,args:{}", args);
        String jpgFileExt = "jpg";
        String resultFilePath = "";
        Convert convert = null;
        try {
            convert = pool.borrowObject();
            String fileExt = FilenameUtils.getExtension(filePath).toLowerCase();
            @Cleanup IPICConvertor picConvertor = fileExt.equals("pdf") ? convert.convertPdftoPic(filePath) : convert.convertMStoPic(filePath);
            if (picConvertor != null) {
                int resultCode = picConvertor.resultCode();
                if (resultCode == 0) {
                    String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
                    picConvertor.convertToJPG(startPageIndex, endPageIndex, 2f, baseDir);
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
        } catch (Exception e) {
            log.error("toJpg happened exception,args:{}", args, e);
        } finally {
            if (convert != null) {
                pool.returnObject(convert);
            }
            stopWatch.stop();
            log.info("toJpg finished,args:{},cost:{}", args, stopWatch.getTime() + "ms");
            return resultFilePath;
        }
    }

    public static String toPng(String filePath, int startPageIndex, int endPageIndex, int startIndex) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String args = String.format("filePath:%s,startPageIndex:%s,endPageIndex:%s,startIndex:%s", filePath, startPageIndex, endPageIndex, startIndex);
        log.info("start convert doc to png,args:{}", args);
        String pngFileExt = "png";
        String resultFilePath = "";
        Convert convert = null;
        try {
            convert = pool.borrowObject();
            String fileExt = FilenameUtils.getExtension(filePath).toLowerCase();
            @Cleanup IPICConvertor picConvertor = fileExt.equals("pdf") ? convert.convertPdftoPic(filePath) : convert.convertMStoPic(filePath);
            if (picConvertor != null) {
                int resultCode = picConvertor.resultCode();
                if (resultCode == 0) {
                    String baseDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
                    picConvertor.convertToPNG(startPageIndex, endPageIndex, 2f, baseDir);
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
        } catch (Exception e) {
            log.error("toPng happened exception,args:{}", args, e);
        } finally {
            if (convert != null) {
                pool.returnObject(convert);
            }
            stopWatch.stop();
            log.info("toPng finished,args:{},cost:{}", args, stopWatch.getTime() + "ms");
            return resultFilePath;
        }
    }

    public static String toHtml(String filePath, int startPageIndex, int endPageIndex, int startIndex) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String args = String.format("filePath:%s,startPageIndex:%s,endPageIndex:%s,startIndex:%s", filePath, startPageIndex, endPageIndex, startIndex);
        log.info("start convert doc to html,args:{}", args);
        String htmlFileExt = "html";
        String resultFilePath = "";
        Convert convert = null;
        try {
            convert = pool.borrowObject();
            @Cleanup IHtmlConvertor htmlConvertor = convert.convertMStoHtml(filePath);
            if (htmlConvertor != null) {
                int resultCode = htmlConvertor.resultCode();
                if (resultCode == 0) {
                    htmlConvertor.setNormal(true);
                    String htmlFilePath=FilePathHelper.getFilePath(filePath,startPageIndex,startIndex,htmlFileExt);
                    htmlConvertor.convertToHtml(htmlFilePath, startPageIndex,endPageIndex);
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
        } catch (Exception e) {
            log.error("toHtml happened exception,args:{}", args, e);
        } finally {
            if (convert != null) {
                pool.returnObject(convert);
            }
            stopWatch.stop();
            log.info("toHtml finished,args:{},cost:{},resultFilePath:{}", args, stopWatch.getTime() + "ms", resultFilePath);
            return resultFilePath;
        }
    }


    public static PageInfo getWordPageCount(String filePath) throws Exception {
        PageInfo pageInfo = new PageInfo();
        Convert convert = null;
        try {
            convert = pool.borrowObject();
            @Cleanup IPICConvertor ipicConvertor = convert.convertMStoPic(filePath);
            int pageCount = ipicConvertor.getPageCount();
            pageInfo.setSuccess(true);
            pageInfo.setPageCount(pageCount);
        } catch (Exception e) {
            log.error("getWordPageCount fail,filepath:{}", filePath, e);
        } finally {
            if (convert != null) {
                pool.returnObject(convert);
            }
            return pageInfo;
        }
    }

}
