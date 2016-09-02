package com.facishare.document.preview.cgi.controller;

import com.facishare.document.preview.cgi.dao.FileTokenDao;
import com.facishare.document.preview.cgi.model.EmployeeInfo;
import com.facishare.fsi.proxy.model.warehouse.n.fileupload.NUploadFileDirect;
import com.facishare.fsi.proxy.service.NFileStorageService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by liuq on 16/8/5.
 */
@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    FileTokenDao fileTokenDao;
    private static final Logger LOG = LoggerFactory.getLogger(PreviewController.class);
    @RequestMapping(method = RequestMethod.GET)
    public void index(HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        Properties props = System.getProperties(); //系统属性
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        printWriter.println("===================java options===================");
        printWriter.println(inputArguments.toString());
        printWriter.println("===================OS===================");
        OperatingSystemMXBean osm = ManagementFactory.getOperatingSystemMXBean();
        printWriter.println("osm.getArch():" + osm.getArch());
        printWriter.println("osm.getAvailableProcessors():" + osm.getAvailableProcessors());
        printWriter.println("osm.getName():" + osm.getName());
        printWriter.println("osm.getVersion():" + osm.getVersion());
        printWriter.println("UserName：" + props.getProperty("user.name"));
        printWriter.println("UserHome：" + props.getProperty("user.home"));
        printWriter.println("=======================Memory============================");
        MemoryMXBean mm = ManagementFactory.getMemoryMXBean();
        printWriter.println("getHeapMemoryUsage " + mm.getHeapMemoryUsage());
        printWriter.println("getNonHeapMemoryUsage " + mm.getNonHeapMemoryUsage());
        printWriter.println("=======================Java RunTime============================");
        printWriter.println("Java Version: " + props.getProperty("java.version"));
        printWriter.println("Java Home: " + props.getProperty("java.home"));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        printWriter.println();
        printWriter.println("Time:" + df.format(new Date()));
    }

    @RequestMapping("/demo")
    public String demo() {
        return "demo";
    }

    @Autowired
    NFileStorageService nFileStorageService;

    @RequestMapping("/upload")
    public void upload(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            LOG.info("begin upload");
            EmployeeInfo employeeInfo = (EmployeeInfo) request.getAttribute("Auth");
            String fileName = file.getOriginalFilename();
            String ext = FilenameUtils.getExtension(fileName);
            byte[] bytes = file.getBytes();
            LOG.info("end upload");
            NUploadFileDirect.Arg arg = new NUploadFileDirect.Arg();
            arg.setData(bytes);
            arg.setEa(employeeInfo.getEa());
            arg.setFileExt(ext);
            arg.setSourceUser(employeeInfo.getSourceUser());
            NUploadFileDirect.Result result = nFileStorageService.nUploadFileDirect(arg, employeeInfo.getEa());
            String npath = result.getFinalNPath();
            response.sendRedirect("/preview/bypath?path=" + npath + "&name=" + URLEncoder.encode(fileName));
        }
        catch (Exception e)
        {
            throw e;
        }

    }

    @RequestMapping("/scroll")
    public String scroll() {
        return "scroll";
    }
}
