package com.facishare.document.preview.cgi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by liuq on 16/8/5.
 */
@Controller
@RequestMapping("/")
public class IndexController{
    private static final Logger LOG = LoggerFactory.getLogger(PreviewController.class);
    @RequestMapping(method = RequestMethod.GET)
    public void index(HttpServletResponse response) throws IOException {
//        Cookie cookie = new Cookie("FSAuthX", "0G60AEUkLW4000343w3bRA0g1CKFthRDMVZyxb2xk8RPaToBhzJa2L0pTyY3Hr7UbO8rHEefF6mPARxQdPwYR2eqWSQ2O1DCUekJzGsICDAJYB45Hw0l4VTzuzzKgiYPserHWmxF0wZmEpQOyojVxgSFubVPbOeJTu9NgrJJtkRFOrF0FMUO1zRAZNdhDWLcUWjTkJ5fYr22zYqctWeiyYtwhAIhsrszP36JTwAMxRo7G97wpP4g2WvmHj13");
//        cookie.setMaxAge(24 * 60 * 60 * 30);
//        response.addCookie(cookie);

        PrintWriter printWriter=response.getWriter();
        Properties props=System.getProperties(); //系统属性
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        printWriter.println("===================java options===================");
        printWriter.println(inputArguments.toString());
        printWriter.println("===================OS===================");
        OperatingSystemMXBean osm = ManagementFactory.getOperatingSystemMXBean();
        printWriter.println("osm.getArch():"+osm.getArch());
        printWriter.println("osm.getAvailableProcessors():"+osm.getAvailableProcessors());
        printWriter.println("osm.getName():"+osm.getName());
        printWriter.println("osm.getVersion():"+osm.getVersion());
        printWriter.println("UserName："+props.getProperty("user.name"));
        printWriter.println("UserHome："+props.getProperty("user.home"));
        printWriter.println("=======================Memory============================");
        MemoryMXBean mm= ManagementFactory.getMemoryMXBean();
        printWriter.println("getHeapMemoryUsage "+mm.getHeapMemoryUsage());
        printWriter.println("getNonHeapMemoryUsage "+mm.getNonHeapMemoryUsage());
        printWriter.println("=======================Java RunTime============================");
        printWriter.println("Java Version: "+props.getProperty("java.version"));
        printWriter.println("Java Home: "+props.getProperty("java.home"));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        printWriter.println();
        printWriter.println("Time:"+df.format(new Date()));// new Date()为获取当前系统时间
    }

    @RequestMapping(value="/preview")
    public String preview()
    {
        return "preview";
    }
}
