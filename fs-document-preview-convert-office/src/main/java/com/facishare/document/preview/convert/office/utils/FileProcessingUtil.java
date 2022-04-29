package com.facishare.document.preview.convert.office.utils;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ZipUtil;
import com.facishare.document.preview.convert.office.constant.ErrorInfoEnum;
import com.facishare.document.preview.convert.office.exception.Office2PdfException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author : [Andy]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/4/21 10:30]
 * @updateUser : [Andy]
 * @updateTime : [2022/4/21 10:30]
 * @updateRemark : [说明本次修改内容]
 */
public class FileProcessingUtil {

  private FileProcessingUtil() {
    throw new Office2PdfException(ErrorInfoEnum.INVALID_REFLECTION_ACCESS);
  }

  public static String getFileType(byte[] fileBate) throws Office2PdfException {
    try (ByteArrayInputStream data = new ByteArrayInputStream(fileBate, 0, 100)) {
      return FileTypeUtil.getType(data);
    } catch (IOException e) {
      throw new Office2PdfException(ErrorInfoEnum.STREAM_CLOSING_ANOMALY, e);
    }
  }

  public static String createDirectory(String filePath) throws Office2PdfException {
    filePath = getRandomFilePath(filePath);
    try {
      FileUtil.mkdir(filePath);
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.UNABLE_CREATE_FOLDER, e);
    }
    return filePath;
  }

  public static String getRandomFilePath(String filePath) {
    return String.valueOf(Paths.get(filePath, UUID.randomUUID().toString()));
  }

  /**
   * getZipByte 将转换好的保存图片的文件夹打成压缩包 并将其转换为字节流之后 删除保存图片的文件夹以及保存压缩包的文件夹极
   *
   * @param office2PngTempPath    保存图片文件夹的父目录
   * @param office2PngZipTempPath 保存压缩包文件夹路径的父目录
   * @return byte[] 类型  将图片压缩包转换为字节数组并返回
   * @throws Office2PdfException UNABLE_CREATE_FOLDER 创建文件夹失败,FAILED_READ_DATA 从压缩包获取字节数组失败,FILE_DELETION_FAILED 删除图片以及压缩包文件夹失败
   */
  public static byte[] getZipByte(String office2PngTempPath, String office2PngZipTempPath) throws Office2PdfException {
    String zipFileName = office2PngZipTempPath + "\\" + UUID.randomUUID() + ".zip";
    byte[] fileBate;
    try {
      fileBate = FileUtil.readBytes((ZipUtil.zip(office2PngTempPath, zipFileName)));
    } catch (Exception e) {
      throw new Office2PdfException(ErrorInfoEnum.FAILED_READ_DATA, e);
    } finally {
      FileUtil.del(office2PngZipTempPath);
      FileUtil.del(office2PngTempPath);
    }
    return fileBate;
  }

  public static String extName(String fileName) {
    return FileNameUtil.extName(fileName);
  }


}
