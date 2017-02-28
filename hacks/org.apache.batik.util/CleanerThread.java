/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.util;

import java.lang.ref.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * One line Class Desc
 * <p>
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org">l449433</a>
 * @version $Id$
 */
public class CleanerThread extends Thread implements AutoCloseable {
  volatile ReferenceQueue queue;
  public static final CleanerThread THREAD = new CleanerThread();

  public synchronized ReferenceQueue getReferenceQueue() {
    return queue;
  }

  /**
   * If objects registered with the reference queue associated with
   * this class implement this interface then the 'cleared' method
   * will be called when the reference is queued.
   */
  public static interface ReferenceCleared {
    /* Called when the reference is cleared */
    void cleared();
  }


  /**
   * A SoftReference subclass that automatically registers with
   * the cleaner ReferenceQueue.
   */
  public abstract static class SoftReferenceCleared extends SoftReference implements ReferenceCleared {
    public SoftReferenceCleared(Object o) {
      super(o, THREAD.getReferenceQueue());
    }
  }


  /**
   * A WeakReference subclass that automatically registers with
   * the cleaner ReferenceQueue.
   */
  public abstract static class WeakReferenceCleared extends WeakReference implements ReferenceCleared {
    public WeakReferenceCleared(Object o) {
      super(o, THREAD.getReferenceQueue());
    }
  }


  /**
   * A PhantomReference subclass that automatically registers with
   * the cleaner ReferenceQueue.
   */
  public abstract static class PhantomReferenceCleared extends PhantomReference implements ReferenceCleared {
    public PhantomReferenceCleared(Object o) {
      super(o, THREAD.getReferenceQueue());
    }
  }

  private static volatile int num = 0;
  private static final int getSeqNo() {
    try {
      throw new RuntimeException("detect stacktrace");
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    return ++num;
  }

  protected CleanerThread() {
    super("Batik CleanerThread-" + getSeqNo());
    queue = new ReferenceQueue();
    setDaemon(true);
    start();
  }

  public void run() {
    ReferenceQueue rq;
    while ((rq = getReferenceQueue()) != null) {
      try {
        Reference ref;
        try {
          ref = rq.remove();
        } catch (InterruptedException ie) {
          break;
        }

        if (ref instanceof ReferenceCleared) {
          ReferenceCleared rc = (ReferenceCleared) ref;
          rc.cleared();
        }
      } catch (ThreadDeath td) {
        throw td;
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
    System.err.println(Thread.currentThread().getName() + " exited");
  }

  /**
   * Stops the cleaner thread. Calling this method is recommended in all long running applications
   * with custom class loaders (e.g., web applications).
   */
  public void close() {
    // try to stop it gracefully
    synchronized (this) {
      queue = null;
    }
    this.interrupt();
    try {
      this.join(500);
    } catch (InterruptedException e) {
      // join failed
    }
    // last resort tentative to kill the cleaner thread
    if (this.isAlive()) {
      this.stop();
    }
  }
}
