package com.n26.schedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n26.domain.Transaction;
import com.n26.lock.Lock;
import com.n26.storage.StatisticsStorage;
import com.n26.storage.TransactionStorage;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class ScheduledStatisticsAggregatorTest {

  private final Lock lock = mock(Lock.class);
  private final TransactionStorage transactionStorage = mock(TransactionStorage.class);
  private final StatisticsStorage statisticsStorage = mock(StatisticsStorage.class);

  private ScheduledStatisticsAggregator sut;

  @Before
  public void setUp() {
    sut = new ScheduledStatisticsAggregator(lock, statisticsStorage, transactionStorage);
  }

  @Test
  public void shouldHavePostConstructAnnotation() throws NoSuchMethodException {
    Class<?> resourceClass = ScheduledStatisticsAggregator.class;
    final Method method = resourceClass.getMethod("init");
    assertTrue(method.isAnnotationPresent(PostConstruct.class));
  }

  @Test
  public void canInitSchedulerAndCallAggregateEverySecond() throws InterruptedException {
    final ArgumentCaptor<DoubleSummaryStatistics> captor =
        ArgumentCaptor.forClass(DoubleSummaryStatistics.class);
    sut.init();
    TimeUnit.SECONDS.sleep(5);
    verify(lock, times(5)).lock();
    verify(lock, times(5)).unlock();
    verify(transactionStorage, times(5)).getLiveTransactions();
    verify(statisticsStorage, times(5)).reset();
    verify(statisticsStorage, times(5)).update(captor.capture());
  }

  @Test
  public void canAggregateStatistics() {
    final ArgumentCaptor<DoubleSummaryStatistics> captor =
        ArgumentCaptor.forClass(DoubleSummaryStatistics.class);
    Collection<Transaction> transactions = new ArrayList<>();
    transactions.add(Transaction.create(10.10, Instant.now()));
    transactions.add(Transaction.create(20.78, Instant.now()));
    transactions.add(Transaction.create(15.33, Instant.now()));
    when(transactionStorage.getLiveTransactions()).thenReturn(transactions);

    sut.aggregate();

    verify(statisticsStorage, times(1)).update(captor.capture());
    verify(statisticsStorage, times(1)).reset();

    assertEquals(3, captor.getValue().getCount());
    assertEquals(15.403333333333334, captor.getValue().getAverage(), 0);
    assertEquals(46.21, captor.getValue().getSum(), 0);
    assertEquals(10.1, captor.getValue().getMin(), 0);
    assertEquals(20.78, captor.getValue().getMax(), 0);
    assertEquals(3.0, captor.getValue().getCount(), 0);
  }
}
