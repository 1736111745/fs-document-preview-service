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
    //文件路径
    private String path;
    private Date createTime;
    private int createYYMMDD;
    //企业账号
    private String ea;
    //总页码
    private int pageCount;
    //Excel工作表元数据
    private List<String> sheetNames;
    //文件夹名称
    private String dirName;
    //文件夹路径
    private String dataDir;
    //员工账号id
    private long employeeId;
    //文件原始大小
    private long docSize;
    //原始文件路径
    private String originalFilePath;//原始文件
    private List<String> filePathList;
    //文档页码宽度
    private int width;
    //pdf转换类型，0表示html 1表示image
    private int pdfConvertType;
}
