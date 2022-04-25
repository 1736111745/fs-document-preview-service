package com.facishare.document.preview.convert.office.service.impl;

import com.facishare.document.preview.common.model.PageInfo;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.service.DocumentPageInfoService;
import com.facishare.document.preview.convert.office.utils.ExcelObjectUtil;
import com.facishare.document.preview.convert.office.utils.PageInfoUtil;
import com.facishare.document.preview.convert.office.utils.ParameterCalibrationUtil;
import com.facishare.document.preview.convert.office.utils.PdfObjectUtil;
import com.facishare.document.preview.convert.office.utils.PptObjectUtil;
import com.facishare.document.preview.convert.office.utils.WordObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * @author Andy
 */
@Slf4j
@Service
public class DocumentPageInfoServiceImpl implements DocumentPageInfoService {
  public PageInfo getPageInfo(String filePath, ByteArrayInputStream fileStream) throws Exception {
    switch (ParameterCalibrationUtil.isDifference(filePath, fileStream)) {
      case DOC:
      case DOCX:
        return PageInfoUtil.getPageInfo(WordObjectUtil.getPageCount(fileStream));
      case XLS:
      case XLSX:
        return ExcelObjectUtil.getSheetNames(fileStream);
      case PPT:
      case PPTX:
        return PageInfoUtil.getPageInfo(PptObjectUtil.getPageCount(fileStream));
      case PDF:
        return PageInfoUtil.getPageInfo(PdfObjectUtil.getPageCount(fileStream));
      default:
        return PageInfoUtil.getPageInfo(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH);
    }
  }
}
