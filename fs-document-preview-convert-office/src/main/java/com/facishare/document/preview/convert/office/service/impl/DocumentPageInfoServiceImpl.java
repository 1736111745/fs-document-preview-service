package com.facishare.document.preview.convert.office.service.impl;

import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.service.DocumentPageInfoService;
import com.facishare.document.preview.convert.office.utils.ExcelObjectUtil;
import com.facishare.document.preview.convert.office.utils.PageInfoUtil;
import com.facishare.document.preview.convert.office.utils.PdfObjectUtil;
import com.facishare.document.preview.convert.office.utils.PptObjectUtil;
import com.facishare.document.preview.convert.office.utils.WordObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Andy
 */
@Slf4j
@Service
public class DocumentPageInfoServiceImpl implements DocumentPageInfoService {
  public PageInfo getPageInfo(byte[] fileBate, FileTypeEnum fileTypeEnum) throws Exception {
    switch (fileTypeEnum) {
      case DOC:
      case DOCX:
        return PageInfoUtil.getPageInfo(WordObjectUtil.getPageCount(fileBate));
      case XLS:
      case XLSX:
        return ExcelObjectUtil.getSheetNames(fileBate);
      case PPT:
      case PPTX:
        return PageInfoUtil.getPageInfo(PptObjectUtil.getPageCount(fileBate));
      default:
        return PageInfoUtil.getPageInfo(PdfObjectUtil.getPageCount(fileBate));
    }
  }
}
