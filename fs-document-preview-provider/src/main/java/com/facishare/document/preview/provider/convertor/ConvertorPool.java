package com.facishare.document.preview.provider.convertor;

import application.dcs.Convert;
import com.facishare.document.preview.common.utils.PathHelper;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ConvertorPool {
    private  String configDir = "";

    private ConvertorPool() {
        String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();
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
    }

    private static class SingletonHolder {
        private static final ConvertorPool INSTANCE = new ConvertorPool();
    }

    private List<ConvertorObject> pool = Collections.synchronizedList(new ArrayList());
    private static final int maxSize = 100;
    private int availSize = 0;
    private int current = 0;

    public static ConvertorPool getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //获取池内一个转换实例
    public synchronized ConvertorObject getConvertor() {
        log.info("availSize:{}", availSize);
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
        Convert convert = new Convert(configDir);
        convert.setTempPath(new PathHelper().getConvertTempPath());
        convert.setAutoDeleteTempFiles(true);
        convert.setHtmlTitle("文档预览");
        convert.setShowTitle(true);
        convert.setShowPic(true);
        convert.setEmptyCount(100, 200);
        convert.setHtmlEncoding("UTF-8");
        convert.setConvertForPhone(true);
        convert.setTimeout(20);
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
