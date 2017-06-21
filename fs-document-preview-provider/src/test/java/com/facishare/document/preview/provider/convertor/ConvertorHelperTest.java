package com.facishare.document.preview.provider.convertor;

import org.junit.Test;

/**
 * Created by lirui on 2017-01-20 13:58.
 */
public class ConvertorHelperTest {
  @Test
  public void toSvg() throws Exception {
//    ThreadFactory tf = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("test-%d").build();
//    for (int i = 0; i < 1; i++) {
//      Thread t1 = tf.newThread(() -> {
//        try {
//          ConvertorHelper.toSvg(System.getenv("HOME") + "/Downloads/dead.docx", 0, 1, 1);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      });
//
//      t1.start();
//      Thread.sleep(1000);
//
//      Thread t2 = tf.newThread(() -> {
//        try {
//          ConvertorHelper.toSvg(System.getenv("HOME") + "/Downloads/live.docx", 0, 1, 1);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      });
//      t2.start();
//      t2.join();
//      t1.join();
//    }
    try {
      ConvertorHelper.toHtml( "/Users/liuq/Downloads/p3/2日可复现bug解决率20170407.xlsx", 0, 1);
      ConvertorHelper.toHtml( "/Users/liuq/Downloads/p3/2日可复现bug解决率20170407.xlsx", 1, 1);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void getSheetInfo() throws Exception {
     String filePath="/Users/liuq/Downloads/x7rqazvx.xls";
     String[][] sheets= ConvertorHelper.getExcelSheetInfo(filePath);
     System.out.println(sheets);

  }
}
