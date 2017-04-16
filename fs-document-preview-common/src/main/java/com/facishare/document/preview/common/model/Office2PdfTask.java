package com.facishare.document.preview.common.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexDirection;

import java.util.Date;

/**
 * Created by liuq on 2017/3/19.
 */
@Entity(value = "Office2PdfTask", noClassnameStored = true)
@Indexes({@Index(fields = {@Field("ea"), @Field("path")})})
@Getter
@Setter
@ToString
public class Office2PdfTask {
    private String ea;
    private String path;
    @Indexed(value = IndexDirection.ASC, name = "createTime", expireAfterSeconds = 120)
    private Date createTime;
    private Date lastModifyTime;
    private int status;
}

