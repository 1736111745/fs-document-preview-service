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

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    private String folderName;

    private String baseDir;

    private long employeeId;

    private long docSize;//原始大小

    private List<String> svgList;

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

    public List<String> getSvgList() {
        return svgList;
    }

    public void setSvgList(List<String> svgList) {
        this.svgList = svgList;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
