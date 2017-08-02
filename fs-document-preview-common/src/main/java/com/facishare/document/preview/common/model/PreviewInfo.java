package com.facishare.document.preview.common.model;

import lombok.Data;
import lombok.ToString;
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
@Data
@ToString
public class PreviewInfo {
    @Id
    private ObjectId id;
    private String path;
    private Date createTime;
    private int createYYMMDD;
    private String ea;
    private int pageCount;
    private int direction;
    private List<String> sheetNames;
    private String dirName;
    private String dataDir;
    private long employeeId;
    private long docSize;//原始大小
    private String originalFilePath;//原始文件
    private List<String> filePathList;
}
