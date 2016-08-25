package com.facishare.document.preview.cgi.dao.impl;

import com.alibaba.fastjson.JSON;
import com.facishare.document.preview.cgi.dao.FileTokenDao;
import com.facishare.document.preview.cgi.model.FileToken;
import com.facishare.document.preview.cgi.model.FileTokenFields;
import com.github.mongo.support.DatastoreExt;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by liuq on 16/8/25.
 */
@Repository
public class FileTokenDaoImpl implements FileTokenDao{
    private static final Logger LOG = LoggerFactory.getLogger(FileTokenDaoImpl.class);
    @Autowired
    private DatastoreExt shareContentDataStore;

    @Override
    public FileToken getInfo(String ea, String fileToken, String downloadUesr) {
        Query<FileToken> query = shareContentDataStore.createQuery(FileToken.class);
        query.field(FileTokenFields.EA).equal(ea)
                .field(FileTokenFields.fileToken).equal(fileToken)
                .field(FileTokenFields.downloadUser).equal(downloadUesr);
        LOG.info("query:{}", JSON.toJSONString(query.toString()));
        return query.get();
    }
}
