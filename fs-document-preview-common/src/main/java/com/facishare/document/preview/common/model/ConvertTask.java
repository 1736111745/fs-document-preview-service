package com.facishare.document.preview.common.model;

import lombok.Data;
import lombok.ToString;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import java.util.Date;

/**
 * Created by liuq on 2017/3/19.
 */
@Entity(value = "ConvertTask", noClassnameStored = true)
@Indexes({
        @Index(fields = {@Field("ea"),@Field("path"),@Field("page")})}
)
@Data
@ToString
public class ConvertTask {
    private String ea;
    private String path;
    private int page;
    private Date createTime;
    private Date lastModifyTime;
    private int status;
}

