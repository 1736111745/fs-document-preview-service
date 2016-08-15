package com.facishare.document.preview.cgi.convertor;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by liuq on 16/8/13.
 */
public class ConvertorPool extends GenericObjectPool {

    private static ConvertorPool instance;

    private ConvertorPool(GenericObjectPoolConfig config) {
        super(new ConvertorFactory(), config);
    }

    public static synchronized ConvertorPool getInstance() {
        if (instance == null) {
            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMaxTotal(20);
            poolConfig.setMinIdle(1);
            instance = new ConvertorPool(poolConfig);
        }
        return instance;
    }
}
