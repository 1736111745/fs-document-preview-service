package com.ayl;

import cn.hutool.core.io.FileTypeUtil;
import com.aspose.slides.PresentationFactory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class TestIsEncrypted {

  @Test
  public void TestIsEncryptedPPt() throws IOException {
    File file = new File("C:\\Users\\anyl9356\\Documents\\Test\\test.pptx");
    InputStream inputStream = Files.newInputStream(file.toPath());
    byte[] bytes = toByteArray(inputStream);
    String name = FileTypeUtil.getType(new ByteArrayInputStream(bytes, 0, 100));
    ByteArrayInputStream fileStream = new ByteArrayInputStream(bytes);
    boolean bb = PresentationFactory.getInstance().getPresentationInfo(fileStream).isEncrypted();
    System.out.print("输出结果：" + bb + "输出文档类型：" + name);
  }

  private static byte[] toByteArray(InputStream in) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024 * 4];
    int n = 0;
    try {
      while ((n = in.read(buffer)) != -1) {
        out.write(buffer, 0, n);
      }
    } catch (IOException e) {

    }
    return out.toByteArray();
  }
}
