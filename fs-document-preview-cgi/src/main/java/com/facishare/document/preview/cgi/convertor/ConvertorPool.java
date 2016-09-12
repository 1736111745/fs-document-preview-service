package com.facishare.document.preview.cgi.convertor;

import application.dcs.Convert;
import com.facishare.document.preview.cgi.utils.PathHelper;

import java.util.ArrayList;
import java.util.List;

public class ConvertorPool {
    private final static String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    private final static String configDir = root + "yozo_config";

    private ConvertorPool() {
    }

    private static ConvertorPool instance = null;
    private List<ConvertorObject> pool = new ArrayList();
    //池内维护了最大为5个实例，可以根据自己的服务器性能调整最大值
    private static final int maxSize = 20;
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
        convert.setEmptyCount(20, 20);
        convert.setHtmlEncoding("UTF-8");
        convert.setConvertForPhone(true);
        convert.setAutoDeleteTempFiles(true);
        convert.setTimeout(60 * 5);
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
