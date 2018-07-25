package com.facishare.document.preview.asyncconvertor.utils;

  import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
import com.facishare.document.preview.common.utils.OfficeApiHelper;
  import com.facishare.document.preview.common.utils.ThumbnailHelper;
  import com.fxiaoke.common.image.SimpleImageInfo;
  import lombok.extern.slf4j.Slf4j;
  import org.apache.commons.io.FileUtils;
  import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

  import java.io.File;
  import java.io.IOException;

@Slf4j
@Component
public class Pdf2ImageHandler {
  @Autowired
  OfficeApiHelper officeApiHelper;
  @Autowired
  ThumbnailHelper thumbnailHelper;

  public String doConvert(ConvertPdf2HtmlMessage message) throws IOException {
    String filePath = message.getFilePath();
    int page = message.getPage() - 1;
    officeApiHelper.convertOffice2Png(filePath, page);
    int width = message.getPageWidth();
    String imageFilePath = FilenameUtils.getFullPathNoEndSeparator(filePath) + "/" + (page + 1) + ".png";
    SimpleImageInfo simpleImageInfo = new SimpleImageInfo(new File(filePath));
    int defaultWidth = 750;//手机文档预览使用
    int aimWidth = defaultWidth;
    if (width > 0) {
      aimWidth = width;
    }
    File imageFile = new File(imageFilePath);
    int aimHeight = aimWidth * simpleImageInfo.getHeight() / simpleImageInfo.getWidth();
    byte[] data = FileUtils.readFileToByteArray(imageFile);
    thumbnailHelper.doThumbnail(data, aimWidth, aimHeight, imageFile);
    return imageFilePath;
  }
}
