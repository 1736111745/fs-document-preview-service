package com.facishare.document.preview.cgi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
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
    @RequestMapping(method = RequestMethod.GET)
    public void index(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter printWriter=response.getWriter();
        Properties props=System.getProperties(); //系统属性
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        printWriter.println("===================java options===================");
        printWriter.println(inputArguments.toString());
        printWriter.println("===================OS===================");
        OperatingSystemMXBean osm = ManagementFactory.getOperatingSystemMXBean();
        //获取操作系统相关信息
        printWriter.println("osm.getArch():"+osm.getArch());
        printWriter.println("osm.getAvailableProcessors():"+osm.getAvailableProcessors());
        printWriter.println("osm.getName():"+osm.getName());
        printWriter.println("osm.getVersion():"+osm.getVersion());
        printWriter.println("UserName："+props.getProperty("user.name"));
        printWriter.println("UserHome："+props.getProperty("user.home"));
        //获取整个虚拟机内存使用情况
        printWriter.println("=======================Memory============================");
        MemoryMXBean mm= ManagementFactory.getMemoryMXBean();
        printWriter.println("getHeapMemoryUsage "+mm.getHeapMemoryUsage());
        printWriter.println("getNonHeapMemoryUsage "+mm.getNonHeapMemoryUsage());
        //获取运行时信息  
        printWriter.println("=======================Java RunTime============================");
        printWriter.println("Java Version: "+props.getProperty("java.version"));
        printWriter.println("Java Home: "+props.getProperty("java.home"));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        printWriter.println();
        printWriter.println("Time:"+df.format(new Date()));// new Date()为获取当前系统时间

    }

}