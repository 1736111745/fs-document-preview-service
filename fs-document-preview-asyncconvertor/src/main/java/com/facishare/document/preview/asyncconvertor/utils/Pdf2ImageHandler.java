package com.facishare.document.preview.asyncconvertor.utils;

  import com.facishare.document.preview.common.model.ConvertPdf2HtmlMessage;
  import com.facishare.document.preview.common.utils.OfficeApiHelper;
  import lombok.extern.slf4j.Slf4j;
  import org.apache.commons.io.FilenameUtils;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Component;

  import java.io.IOException;

@Slf4j
@Component
public class Pdf2ImageHandler {
  @Autowired
  OfficeApiHelper officeApiHelper;

  public String doConvert(ConvertPdf2HtmlMessage message) throws IOException {
    String filePath = message.getFilePath();
    int page = message.getPage();
    officeApiHelper.convertOffice2Png(filePath, page);
    return FilenameUtils.getFullPathNoEndSeparator(filePath) + "/" + (page + 1) + ".png";
  }
}
