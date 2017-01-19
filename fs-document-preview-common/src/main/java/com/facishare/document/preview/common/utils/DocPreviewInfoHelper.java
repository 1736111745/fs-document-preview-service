package com.facishare.document.preview.common.utils;

import com.facishare.document.preview.common.model.DocType;
import com.facishare.document.preview.common.model.PreviewInfo;

/**
 * Created by liuq on 2017/1/10.
 */
public class DocPreviewInfoHelper {
    public static PreviewInfo getPreviewInfo(String filePath) throws Exception {
        String docFormat = "", previewFormat = "", contentType = "";
        DocType docType = DocTypeHelper.getDocType(filePath);
        switch (docType) {
            case Word: {
                docFormat = "101";
                previewFormat = "1";
                contentType = "image/png";
                break;
            }
            case Excel: {
                docFormat = "102";
                previewFormat = "2";
                contentType = "text/html";
                break;
            }
            case PPT: {
                docFormat = "103";
                previewFormat = "1";
                contentType = "image/png";
                break;
            }
            case PDF: {
                docFormat = "104";
                previewFormat = "1";
                contentType = "image/png";
            }
        }
        PreviewInfo docPreviewInfo = PreviewInfo.builder().previewFormat(previewFormat).documentFormat(docFormat).contentType(contentType).build();
        return docPreviewInfo;
    }
}