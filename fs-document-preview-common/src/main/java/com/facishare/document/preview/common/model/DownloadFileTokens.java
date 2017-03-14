package com.facishare.document.preview.common.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.util.Date;

/**
 * Created by liuq on 16/8/25.
 */
@Data
public class DownloadFileTokens {
    @Id
    private ObjectId _id;
    @Property(FileTokenFields.EA)
    private String EA;
    @Property(FileTokenFields.eaYearMonth)
    private String eaYearMonth;
    @Property(FileTokenFields.fileToken)
    private String fileToken;
    @Property(FileTokenFields.fileType)
    private String fileType;
    @Property(FileTokenFields.fileName)
    private String fileName;
    @Property(FileTokenFields.filePath)
    private String filePath;
    @Property(FileTokenFields.createTime)
    private Date createTime;
    @Property(FileTokenFields.warehouseType)
    private String warehouseType;
    @Property(FileTokenFields.downloadUser)
    private String downloadUser;
    @Property(FileTokenFields.downloadSecurityGroup)
    private String downloadSecurityGroup;
    @Property(FileTokenFields.zippedFilesStructure)
    private String zippedFilesStructure;
}

