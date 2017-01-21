package com.facishare.document.preview.provider.convertor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Test;

import java.util.concurrent.ThreadFactory;

/**
 * Created by lirui on 2017-01-20 13:58.
 */
public class ConvertorHelperTest {
  @Test
  public void toSvg() throws Exception {
    ThreadFactory tf = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("test-%d").build();
    for (int i = 0; i < 1000; i++) {
////      Thread t1 = tf.newThread(() -> {
////        try {
////          ConvertorHelper.toSvg(System.getenv("HOME") + "/Downloads/dead.docx", 0, 1, 1);
////        } catch (Exception e) {
////          e.printStackTrace();
////        }
////      });
////
////      t1.start();
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
//      //t1.join();
      ConvertorHelper.toSvg(System.getenv("HOME") + "/Users/liuq/Downloads/1pagedocx.docx", 0, 1, 1);
    }
  }
}
