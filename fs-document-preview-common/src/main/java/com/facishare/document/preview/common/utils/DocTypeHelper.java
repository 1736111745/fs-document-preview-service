package com.facishare.document.preview.common.utils;


import com.facishare.document.preview.common.model.DocType;
import org.apache.commons.io.FilenameUtils;

/**
 * Created by liuq on 16/9/9.
 */
public class DocTypeHelper {
    public static DocType getDocType(String name) {
        String extension = FilenameUtils.getExtension(name).toLowerCase();
        switch (extension) {
            case "doc":
            case "docx":
                return DocType.Word;
            case "xls":
            case "xlsx":
                return DocType.Excel;
            case "ppt":
            case "pptx":
                return DocType.PPT;
            case "pdf":
                return DocType.PDF;
            default:
                return DocType.Other;
        }
    }
}
