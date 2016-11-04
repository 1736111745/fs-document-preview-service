package com.facishare.document.preview.cgi.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

/**
 * Created by liuq on 2016/11/4.
 */
@Entity(value = "DocPreviewInfo", noClassnameStored = true)
@Indexes({
        @Index(fields = {@Field("path")})}
)
public class DocPreviewInfo extends  PreviewInfo {

}
