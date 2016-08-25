package com.facishare.document.preview.cgi.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.util.Date;

/**
 * Created by liuq on 16/8/25.
 */
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

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getEA() {
        return EA;
    }

    public void setEA(String EA) {
        this.EA = EA;
    }

    public String getEaYearMonth() {
        return eaYearMonth;
    }

    public void setEaYearMonth(String eaYearMonth) {
        this.eaYearMonth = eaYearMonth;
    }

    public String getFileToken() {
        return fileToken;
    }

    public void setFileToken(String fileToken) {
        this.fileToken = fileToken;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }
    public String getDownloadUser() {
        return downloadUser;
    }

    public void setDownloadUser(String downloadUser) {
        this.downloadUser = downloadUser;
    }

    public String getDownloadSecurityGroup() {
        return downloadSecurityGroup;
    }

    public void setDownloadSecurityGroup(String downloadSecurityGroup) {
        this.downloadSecurityGroup = downloadSecurityGroup;
    }

    public String getZippedFilesStructure() {
        return zippedFilesStructure;
    }

    public void setZippedFilesStructure(String zippedFilesStructure) {
        this.zippedFilesStructure = zippedFilesStructure;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(String warehouseType) {
        this.warehouseType = warehouseType;
    }
}

