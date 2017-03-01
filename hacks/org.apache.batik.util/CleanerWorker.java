package org.apache.batik.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * 只需要启动一个工作线程
 * <p>
 * Created by lirui on 2017-02-28 16:10.
 */
public class CleanerWorker implements Runnable, AutoCloseable {
  private boolean running;
  private ReferenceQueue queue = new ReferenceQueue();

  public ReferenceQueue getQueue() {
    return queue;
  }

  @Override
  public void run() {
    while (running) {
      try {
        Reference ref;
        ref = queue.remove(3000L);
        if (ref == null) {
          continue;
        }

        if (ref instanceof CleanerThread.ReferenceCleared) {
          CleanerThread.ReferenceCleared rc = (CleanerThread.ReferenceCleared) ref;
          rc.cleared();
        }
        //XXX: 是否会把使用中的给清理掉呢？
        ref.clear();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    System.out.println(Thread.currentThread().getName() + " exited");
  }

  @Override
  public void close() throws Exception {
    running = false;
  }
}
