package com.n26.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.stereotype.Component;

/** Lock need as global locking mechanics for whole process. */
@Component
public class Lock {
  /**
   * ReentrantReadWriteLock is using for synchronisation between threads instead classic
   * synchronisation as it require more resources
   */
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  public void lock() {
    lock.writeLock().lock();
  }

  public void unlock() {
    lock.writeLock().unlock();
  }
}
