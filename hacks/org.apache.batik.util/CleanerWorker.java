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
    running = true;
    long last = System.currentTimeMillis();
    // 如果十分钟没有任务，那就可以退出了
    while (running && (System.currentTimeMillis() - last < 600000L)) {
      try {
        Reference ref;
        ref = queue.remove(3000L);
        if (ref == null) {
          continue;
        }
        last = System.currentTimeMillis();
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
    String name = Thread.currentThread().getName();
    System.out.println("WARN " + name + " exited, loader: " + CleanerWorker.class.getClassLoader());
  }

  @Override
  public void close() throws Exception {
    running = false;
  }
}
