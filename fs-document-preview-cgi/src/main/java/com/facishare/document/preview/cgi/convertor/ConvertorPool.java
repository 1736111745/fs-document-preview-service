package com.facishare.document.preview.cgi.convertor;

/**
 * Created by liuq on 16/8/8.
 */

import application.dcs.Convert;

import java.util.ArrayList;
import java.util.List;

public class ConvertorPool {
    private final static String root=Thread.currentThread().getContextClassLoader().getResource("").getPath();
    private final static String configDir= root+"Config";
    private ConvertorPool() {}
    private static ConvertorPool instance = null;
    private List<ConvertorObject> pool = new ArrayList<>();
    private static final int maxSize = 10;
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
    public synchronized void releaseConvertor(ConvertorObject convertorObject) {
        for (ConvertorObject co : pool) {
            if (co == convertorObject) {
                co.setAvailable(true);
                availSize++;
                notify();
                break;
            }
        }
    }
    private synchronized ConvertorObject getIdleConvertor() {
        for (ConvertorObject co : pool) {
            if (co.isAvailable()) {
                co.setAvailable(false);
                availSize--;
                return co;
            }
        }
        return null;
    }
    private synchronized ConvertorObject createNewConvertor() {
        ConvertorObject co = new ConvertorObject();
        co.setId(++current);
        co.setConvertor(new Convert(configDir));
        co.setAvailable(false);
        pool.add(co);
        return co;
    }
}

