package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.cgi.model.DocPageInfo;
import com.facishare.document.preview.common.utils.DocType;
import com.facishare.document.preview.common.utils.DocTypeHelper;

/**
 * Created by liuq on 2016/12/15.
 */
public class DocPageInfoHelper {
    public static DocPageInfo getDocPageInfo(String filePath) throws Exception {
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
        DocPageInfo docPageInfo = new DocPageInfo();
        docPageInfo.setContentType(contentType);
        docPageInfo.setDocumentFormat(docFormat);
        docPageInfo.setPreviewFormat(previewFormat);
        return docPageInfo;
    }
}
