package com.facishare.document.preview.cgi.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * Created by liuq on 16/8/16.
 */
@Entity(value = "PreviewInfo", noClassnameStored = true)
@Indexes({
        @Index(fields = {@Field("path")})}
)
public class PreviewInfo {
    @Id
    private ObjectId id;

    private String path;

    private Date createTime;

    private int createYYMMDD;

    private String ea;

    private int pageCount;

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    private String dirName;

    private String dataDir;

    private long employeeId;

    private long docSize;//原始大小

    private String originalFilePath;//原始文件

    private List<String> filePathList;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getCreateYYMMDD() {
        return createYYMMDD;
    }

    public void setCreateYYMMDD(int createYYMMDD) {
        this.createYYMMDD = createYYMMDD;
    }

    public String getEa() {
        return ea;
    }

    public void setEa(String ea) {
        this.ea = ea;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public long getDocSize() {
        return docSize;
    }

    public void setDocSize(long docSize) {
        this.docSize = docSize;
    }

    public List<String> getFilePathList() {
        return filePathList;
    }

    public void setFilePathList(List<String> filePathList) {
        this.filePathList = filePathList;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getOriginalFilePath() {
        return originalFilePath;
    }

    public void setOriginalFilePath(String originalFilePath) {
        this.originalFilePath = originalFilePath;
    }
}
