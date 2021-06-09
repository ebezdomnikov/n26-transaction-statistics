package com.n26.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n26.domain.Stats;
import com.n26.domain.Transaction;
import com.n26.lock.Lock;
import com.n26.storage.StatisticsStorage;
import com.n26.storage.TransactionStorage;
import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import org.junit.Before;
import org.junit.Test;

public class TransactionStatisticsServiceTest {
  private TransactionStatisticsService sut;

  private final Lock lock = mock(Lock.class);
  private final TransactionStorage transactionStorage = mock(TransactionStorage.class);
  private final StatisticsStorage statisticsStorage = mock(StatisticsStorage.class);

  @Before
  public void setUp() {
    clearInvocations(lock, transactionStorage, statisticsStorage);
    sut = new TransactionStatisticsService(lock, transactionStorage, statisticsStorage);
  }

  @Test
  public void canAddTransactionAndUpdateStat() {
    Transaction transaction = Transaction.create(10.12, Instant.now());
    sut.addTransactionAndUpdateStat(transaction);
    verify(lock, times(1)).lock();
    verify(transactionStorage, times(1)).addTransaction(transaction);
    verify(statisticsStorage, times(1)).update(10.12);
    verify(lock, times(1)).unlock();
  }

  @Test
  public void canGetStatistics() {
    Stats stats = new Stats(new DoubleSummaryStatistics());
    when(statisticsStorage.getStatistics()).thenReturn(stats);
    final Stats actual = sut.getStatistics();
    assertEquals(stats, actual);
    verify(statisticsStorage, times(1)).getStatistics();
    verify(lock, times(1)).lock();
    verify(lock, times(1)).unlock();
  }

  @Test
  public void canClearTransactionsAndResetStat() {
    sut.clearTransactionsAndResetStat();
    verify(statisticsStorage, times(1)).reset();
    verify(transactionStorage, times(1)).clear();
    verify(lock, times(1)).lock();
    verify(lock, times(1)).unlock();
  }
}
