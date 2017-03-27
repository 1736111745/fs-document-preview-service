package com.facishare.document.preview.common.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexDirection;

import java.util.Date;

/**
 * Created by liuq on 2017/3/19.
 */
@Entity(value = "ConvertTask", noClassnameStored = true)
@Indexes({@Index(fields = {@Field("ea"), @Field("path"), @Field("page")})})
@Getter
@Setter
@ToString
public class ConvertTask {
    private String ea;
    private String path;
    private Integer page;
    @Indexed(value = IndexDirection.ASC, name = "createTime", expireAfterSeconds = 120)
    private Date createTime;
    private Date lastModifyTime;
    private int status;

//    @Override
//    public boolean equals(Object obj) {
//        ConvertTask convertTask = (ConvertTask) obj;
//        return path.hashCode() == convertTask.hashCode() && ea.hashCode() == convertTask.ea.hashCode() && page.hashCode() == convertTask.page.hashCode();
//    }
//
//    @Override
//    public int hashCode() {
//        return ea.hashCode() * path.hashCode() * page.hashCode();
//    }
}

