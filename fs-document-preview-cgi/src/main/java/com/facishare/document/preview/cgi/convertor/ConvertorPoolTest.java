package com.facishare.document.preview.cgi.convertor;


import application.dcs.Convert;
import com.facishare.document.preview.cgi.utils.SampleUUID;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liuq on 16/8/15.
 */
public class ConvertorPoolTest {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 1; i++) {
            MyTask myTask = new MyTask(i);
            executor.execute(myTask);
        }
        executor.shutdown();
    }

    static class MyTask implements Runnable {

        private int id;

        public MyTask(int id)
        {
            this.id=id;
        }

        @Override
        public void run() {
            try {
                Convert convert= (Convert) ConvertorPool.getInstance().borrowObject();
                int code=convert.convertPdfToHtml("/Users/liuq/Temp/b.pdf","/Users/liuq/Temp/Result/"+ SampleUUID.getUUID()+".html");
                System.out.println("code:"+code);
                System.out.println("thread:"+id);
                ConvertorPool.getInstance().returnObject(convert);
                System.out.println("creat count:"+ConvertorPool.getInstance().getCreatedCount());
                System.out.println("borrowed count:"+ConvertorPool.getInstance().getBorrowedCount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
