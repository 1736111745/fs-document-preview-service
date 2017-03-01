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

import java.lang.ref.PhantomReference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * One line Class Desc
 * <p>
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org">l449433</a>
 * @version $Id$
 */
public class CleanerThread {
  private static final CleanerWorker WORKER = new CleanerWorker();
  private static final Thread CONTAINER;

  static {
    CONTAINER = new Thread(WORKER, "Batik CleanerThread");
    CONTAINER.start();
    System.out.println("WARN thread started: " + CONTAINER.getName() + ", loader: " + CleanerThread.class.getClassLoader());
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
      super(o, WORKER.getQueue());
    }
  }


  /**
   * A WeakReference subclass that automatically registers with
   * the cleaner ReferenceQueue.
   */
  public abstract static class WeakReferenceCleared extends WeakReference implements ReferenceCleared {
    public WeakReferenceCleared(Object o) {
      super(o, WORKER.getQueue());
    }
  }


  /**
   * A PhantomReference subclass that automatically registers with
   * the cleaner ReferenceQueue.
   */
  public abstract static class PhantomReferenceCleared extends PhantomReference implements ReferenceCleared {
    public PhantomReferenceCleared(Object o) {
      super(o, WORKER.getQueue());
    }
  }

  protected CleanerThread() {
  }
}
