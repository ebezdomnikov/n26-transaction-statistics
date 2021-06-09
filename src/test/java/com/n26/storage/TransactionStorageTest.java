package com.n26.storage;

import static com.n26.domain.Transaction.create;
import static java.time.Instant.now;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;

public class TransactionStorageTest {

  private TransactionStorage sut;

  @Before
  public void setUp() {
    sut = new TransactionStorage();
  }

  @Test
  public void canGetOnlyLiveTransaction() throws InterruptedException {
    sut.addTransaction(create(10.00D, now().minusMillis(60000)));
    sut.addTransaction(create(10.00D, now().minusMillis(58000)));
    sut.addTransaction(create(12.00D, now().minusMillis(57000)));
    sut.addTransaction(create(14.00D, now().minusMillis(56000)));
    assertEquals(3, sut.getLiveTransactions().size());
    TimeUnit.MILLISECONDS.sleep(1000);
    assertEquals(3, sut.getLiveTransactions().size());
    TimeUnit.MILLISECONDS.sleep(1000);
    assertEquals(2, sut.getLiveTransactions().size());
    TimeUnit.MILLISECONDS.sleep(1000);
    assertEquals(1, sut.getLiveTransactions().size());
    TimeUnit.MILLISECONDS.sleep(1000);
    assertEquals(0, sut.getLiveTransactions().size());
  }

  @Test
  public void canClearTransactions() {
    sut.addTransaction(create(10.00D, now().minusMillis(60000)));
    sut.addTransaction(create(10.00D, now().minusMillis(58000)));
    sut.addTransaction(create(12.00D, now().minusMillis(57000)));
    sut.addTransaction(create(14.00D, now().minusMillis(56000)));
    assertEquals(3, sut.getLiveTransactions().size());
    sut.clear();
    assertEquals(0, sut.getLiveTransactions().size());
  }
}
