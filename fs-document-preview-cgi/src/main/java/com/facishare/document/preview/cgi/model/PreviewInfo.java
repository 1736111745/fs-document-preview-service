package com.facishare.document.preview.cgi.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Date;

/**
 * Created by liuq on 16/8/16.
 */
@Entity(value = "PreviewInfo", noClassnameStored = true)
@Indexes({
        @Index(fields = {@Field("path")}),@Index(fields = {@Field("htmlName")})}
)
public class PreviewInfo {
    @Id
    private ObjectId id;

    private String path;

    private String htmlName;

    private String htmlFilePath;

    private Date createTime;

    private int createYYMMDD;

    private String ea;

    private long employeeId;

    private long docSize;//原始大小

    private long htmlSize;//解析后的大小

    public long getDocSize() {
        return docSize;
    }

    public void setDocSize(long docSize) {
        this.docSize = docSize;
    }

    public long getHtmlSize() {
        return htmlSize;
    }

    public void setHtmlSize(long htmlSize) {
        this.htmlSize = htmlSize;
    }

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

    public String getHtmlName() {
        return htmlName;
    }

    public void setHtmlName(String htmlName) {
        this.htmlName = htmlName;
    }

    public String getHtmlFilePath() {
        return htmlFilePath;
    }

    public void setHtmlFilePath(String htmlFilePath) {
        this.htmlFilePath = htmlFilePath;
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

}
