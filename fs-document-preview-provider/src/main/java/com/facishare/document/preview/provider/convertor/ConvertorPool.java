package com.facishare.document.preview.provider.convertor;

import application.dcs.Convert;
import com.facishare.document.preview.common.utils.PathHelper;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConvertorPool {
    private final static String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();

    private static String getConfigDir() {
        String configDir;
        String profile = System.getProperty("spring.profiles.active");
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
        return configDir;
    }


    private ConvertorPool() {
    }

    private static ConvertorPool instance = null;
    private List<ConvertorObject> pool = new ArrayList();
    private static final int maxSize = 16;
    private int availSize = 0;
    private int current = 0;

    public static ConvertorPool getInstance() {
        if (instance == null) {
            instance = new ConvertorPool();
        }
        return instance;
    }

    //获取池内一个转换实例
    public synchronized ConvertorObject getConvertor() {
        if (availSize > 0) {
            return getIdleConvertor();
        } else if (pool.size() < maxSize) {
            return createNewConvertor();
        } else {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getConvertor();
        }
    }

    //使用完成需要还给池内
    public synchronized void returnConvertor(ConvertorObject convertor) {
        for (ConvertorObject co : pool) {
            if (co == convertor) {
                co.available = true;
                availSize++;
                notify();
                break;
            }
        }
    }

    private synchronized ConvertorObject getIdleConvertor() {
        //LOG.info("convert pool availSize:{}", availSize);
        for (ConvertorObject co : pool) {
            if (co.available) {
                co.available = false;
                availSize--;
                return co;
            }
        }
        return null;
    }

    private synchronized ConvertorObject createNewConvertor() {
        ConvertorObject co = new ConvertorObject(++current);
        co.convertor = createConvert();
        co.available = false;
        pool.add(co);
        return co;
    }

    private Convert createConvert() {
        Convert convert = new Convert(getConfigDir());
        convert.setTempPath(new PathHelper().getConvertTempPath());
        convert.setAutoDeleteTempFiles(true);
        convert.setHtmlTitle("文档预览");
        convert.setShowTitle(true);
        convert.setShowPic(true);
        convert.setEmptyCount(100, 200);
        convert.setHtmlEncoding("UTF-8");
        convert.setConvertForPhone(true);
        convert.setTimeout(30);
        return convert;
    }

    public class ConvertorObject {
        public ConvertorObject(int id) {
            this.id = id;
        }

        public int id;
        public Convert convertor;
        public boolean available;
    }
}
