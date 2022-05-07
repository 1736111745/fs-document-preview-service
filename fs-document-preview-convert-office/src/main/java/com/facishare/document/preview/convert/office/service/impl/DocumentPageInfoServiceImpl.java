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
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Andy
 */
@Slf4j
@Service
public class DocumentPageInfoServiceImpl implements DocumentPageInfoService {
  public PageInfo getPageInfo(MultipartFile file, FileTypeEnum fileTypeEnum) {
    switch (fileTypeEnum) {
      case DOC:
      case DOCX:
        return PageInfoUtil.getPageInfo(WordObjectUtil.getPageCount(file));
      case XLS:
      case XLSX:
        return ExcelObjectUtil.getSheetNames(file);
      case PPT:
      case PPTX:
        return PageInfoUtil.getPageInfo(PptObjectUtil.getPageCount(file));
      default: return PageInfoUtil.getPageInfo(PdfObjectUtil.getPageCount(file));
    }
  }
}
