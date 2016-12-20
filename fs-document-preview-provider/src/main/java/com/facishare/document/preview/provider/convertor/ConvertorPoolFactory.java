package com.facishare.document.preview.provider.convertor;

import application.dcs.Convert;
import com.facishare.document.preview.common.utils.PathHelper;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by liuq on 2016/12/20.
 */
public class ConvertorPoolFactory {
    private GenericObjectPool pool;

    public ConvertorPoolFactory(Config config) {
        ConvertorFactory factory = new ConvertorFactory();
        pool = new GenericObjectPool(factory, config);
    }

    public Convert getConvert() throws Exception {
        return (Convert) pool.borrowObject();
    }

    public void releaseConvert(Convert convert) throws Exception {
        pool.returnObject(convert);
    }

    class ConvertorFactory extends BasePoolableObjectFactory {

        private String configDir = "";

        public ConvertorFactory() {
            this.configDir = getConfigDir();
        }

        private String getConfigDir() {
            String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String configDir;
            String profile = System.getProperty("spring.profiles.active");
            if (profile == null || profile.length() == 0) {
                profile = System.getProperty("process.profile");
            }
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
            }
            return configDir;
        }

        @Override
        public Object makeObject() throws Exception {
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

        public void destroyObject(Object obj) throws Exception {
            if (obj instanceof Convert) {
                ((Convert) obj).close();
            }
        }

        public boolean validateObject(Object obj) {
            return true;
        }
    }
}
