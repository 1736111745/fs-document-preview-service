package com.facishare.document.preview.convert.office.utils;


import cn.hutool.core.io.file.FileNameUtil;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.constant.FileTypeEnum;
import com.facishare.document.preview.convert.office.constant.Office2PdfException;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public class ParamCheckUtil {

  public static FileTypeEnum getFileType(String filePath) throws Office2PdfException {
    try {
      return FileTypeEnum.valueOf(FileNameUtil.extName(filePath).toUpperCase());
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH, e);
    }
  }

  public static void isExcelType(String filePath) throws Office2PdfException {
    try {
      FileTypeEnum fileTypeEnum= FileTypeEnum.valueOf(FileNameUtil.extName(filePath).toUpperCase());
      if ((fileTypeEnum.compareTo(FileTypeEnum.XLS)==0)||(fileTypeEnum.compareTo(FileTypeEnum.XLSX)==0)){
      }else {
        throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_EXCEL);
      }
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_TYPES_DO_NOT_MATCH, e);
    }
  }

  public static byte[] getFileBytes(MultipartFile file) throws Office2PdfException {
    try {
      byte[] fileBytes = file.getBytes();
      if (fileBytes.length <=50) {
        throw new Office2PdfException(ErrorInfoEnum.EMPTY_FILE);
      }
      return fileBytes;
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.FILE_STREAM_ERROR);
    }
  }
  
  

}
