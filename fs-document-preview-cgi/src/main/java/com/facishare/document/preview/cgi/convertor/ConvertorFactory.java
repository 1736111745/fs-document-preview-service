package com.facishare.document.preview.cgi.convertor;


import application.dcs.Convert;
import com.facishare.document.preview.cgi.utils.PathHelper;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by liuq on 16/8/8.
 */

public class ConvertorFactory extends BasePooledObjectFactory<Convert> {
    private final static String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    private final static String configDir = root + "yozo_config";

    @Override
    public Convert create() throws Exception {
        Convert convert = new Convert(configDir);
        convert.setTempPath(new PathHelper().getConvertTempPath());
        convert.setAutoDeleteTempFiles(true);
        convert.setHtmlTitle("文档预览");
        convert.setShowTitle(true);
        convert.setShowPic(true);
        convert.setEmptyCount(20,20);
        convert.setHtmlEncoding("UTF-8");
        convert.setConvertForPhone(true);
        convert.setAutoDeleteTempFiles(true);
        convert.setTimeout(60 * 5);
        return convert;
    }

    @Override
    public PooledObject<Convert> wrap(Convert convert) {
        return new DefaultPooledObject<>(convert);
    }

    @Override
    public void passivateObject(PooledObject<Convert> object) {
    }

    @Override
    public boolean validateObject(PooledObject<Convert> object) {
        return true;
    }

    @Override
    public void destroyObject(PooledObject<Convert> object) {
        object.getObject().close();
    }
}

