package com.facishare.document.preview.common.dao.impl;

import com.facishare.document.preview.common.dao.FileTokenDao;
import com.facishare.document.preview.common.model.DownloadFileTokens;
import com.facishare.document.preview.common.model.FileTokenFields;
import com.github.mongo.support.DatastoreExt;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by liuq on 16/8/25.
 */
@Repository
public class FileTokenDaoImpl implements FileTokenDao {
    @Autowired
    private DatastoreExt shareContentDataStore;

    @Override
    public DownloadFileTokens getInfo(String ea, String fileToken, String downloadUser) {
        Query<DownloadFileTokens> query = shareContentDataStore.createQuery(DownloadFileTokens.class);
        query.field(FileTokenFields.EA).equal(ea)
                .field(FileTokenFields.fileToken).equal(fileToken)
                .field(FileTokenFields.downloadUser).equal(downloadUser);
        return query.get();
    }
}
