package com.facishare.document.preview.cgi.utils;

import application.dcs.Convert;
import application.dcs.IPICConvertor;
import com.facishare.document.preview.cgi.convertor.ConvertorPool;
import com.facishare.document.preview.cgi.dao.PreviewInfoDao;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;


/**
 * Created by liuq on 16/8/16.
 */

public class ConvertorHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ConvertorHelper.class);
    @Autowired
    PreviewInfoDao previewInfoDao;

    private EmployeeInfo employeeInfo;

    public ConvertorHelper(EmployeeInfo employeeInfo) {
        this.employeeInfo = employeeInfo;
    }

    public int getPageCount(String path, byte[] bytes) throws Exception {
        LOG.info("begin get convertor!");
        Convert convert = (Convert) ConvertorPool.getInstance().borrowObject();
        LOG.info("end get convertor!");
        try {
            Slf4JStopWatch stopWatch = new Slf4JStopWatch();
            stopWatch.setTimeThreshold(0);
            stopWatch.start();
            PathHelper pathHelper = new PathHelper(employeeInfo.getEa());
            String tempFilePath = pathHelper.getTempFilePath(path, bytes);
            LOG.info("begin get IPICConvertor");
            IPICConvertor ipicConvertor = convert.convertMStoPic(tempFilePath);
            LOG.info("end get IPICConvertor");
            LOG.info("begin get page count");
            int pageCount=ipicConvertor.getPageCount();
            LOG.info("end get page count");
            return pageCount;
        } catch (Exception e) {
            LOG.error("error info:" + e.getStackTrace());
            return -1;
        } finally {
            ConvertorPool.getInstance().returnObject(convert);
        }
    }

    public String doConvert(String path, String baseDir, String name, byte[] bytes, int page) throws Exception {
        LOG.info("begin get convertor!");
        Convert convert = (Convert) ConvertorPool.getInstance().borrowObject();
        LOG.info("end get convertor!");
        PathHelper pathHelper = new PathHelper(employeeInfo.getEa());
        String tempFilePath = pathHelper.getTempFilePath(path, bytes);
        try {
            Slf4JStopWatch stopWatch = new Slf4JStopWatch();
            stopWatch.setTimeThreshold(0);
            stopWatch.start();
            convert.setHtmlName(name);
            LOG.info("begin get IPICConvertor");
            IPICConvertor ipicConvertor = convert.convertMStoPic(tempFilePath);
            LOG.info("end get IPICConvertor");
            int resultcode = ipicConvertor.resultCode();
            if (resultcode == 0) {
                LOG.info("begin get svg,svg folder:{}", baseDir);
                int code = ipicConvertor.convertToSVG(page, page, baseDir);
                stopWatch.stop();
                LOG.info("end get svg");
                LOG.info("file:{},page:{},length:{},code:{},cost:{} ms", path, page, bytes.length / 1024, code, stopWatch.getElapsedTime());
                String svgFilePath = baseDir + "/" + (page + 1) + ".svg";
                File file = new File(svgFilePath);
                if (file.exists()) {
                    return FilenameUtils.getBaseName(baseDir) + "/" + (page + 1) + ".svg";
                } else {
                    return "";
                }
            } else
                return "";
        } catch (Exception e) {
            LOG.error("error info:" + e.getStackTrace());
            return "";
        } finally {
            FileUtils.deleteQuietly(new File(tempFilePath));
            ConvertorPool.getInstance().returnObject(convert);
        }
    }
}
