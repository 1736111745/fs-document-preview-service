package com.facishare.document.preview.cgi.utils;

import application.dcs.Convert;
import com.facishare.document.preview.cgi.convertor.ConvertorPool;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import org.apache.commons.io.FileUtils;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Created by liuq on 16/8/16.
 */
public class ConvertorHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ConvertorHelper.class);
    private EmployeeInfo employeeInfo;

    public ConvertorHelper(EmployeeInfo employeeInfo) {
        this.employeeInfo = employeeInfo;
    }

    public String doConvert(String path, byte[] bytes, String fileName) throws Exception {
        Convert convert = (Convert) ConvertorPool.getInstance().borrowObject();
        try {
            Slf4JStopWatch stopWatch = new Slf4JStopWatch();
            stopWatch.setTimeThreshold(0);
            stopWatch.start();
            PathHelper pathHelper = new PathHelper(employeeInfo);
            String tempFilePath = pathHelper.getTempFilePath(path, bytes);
            String htmlFilePath = pathHelper.getHtmlFilePath(path);
            int code = path.toLowerCase().contains(".pdf") ? convert.convertPdfToHtml(tempFilePath, htmlFilePath) : convert.convertMStoHtmlOfSvg(tempFilePath, htmlFilePath);
            FileUtils.deleteQuietly(new File(tempFilePath));
            String takeInfo = stopWatch.stop();
            LOG.info("code:{},takeInfo:{}", code, takeInfo);
            return code==0?htmlFilePath:"";
        } catch (Exception e) {
            LOG.error("error info:" + e.getStackTrace());
            return "";
        } finally {
            ConvertorPool.getInstance().returnObject(convert);
        }
    }
}
