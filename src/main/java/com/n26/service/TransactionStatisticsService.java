package com.n26.service;

import com.n26.domain.Stats;
import com.n26.domain.Transaction;
import com.n26.lock.Lock;
import com.n26.storage.StatisticsStorage;
import com.n26.storage.TransactionStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionStatisticsService {

  private final Lock lock;
  private final TransactionStorage transactionStorage;
  private final StatisticsStorage statisticsStorage;

  public void addTransactionAndUpdateStat(final Transaction transaction) {
    lock.lock();
    try {
      transactionStorage.addTransaction(transaction);
      statisticsStorage.update(transaction.getAmount().doubleValue());
    } finally {
      lock.unlock();
    }
  }

  public Stats getStatistics() {
    lock.lock();
    try {
      return statisticsStorage.getStatistics();
    } finally {
      lock.unlock();
    }
  }

  public void clearTransactionsAndResetStat() {
    lock.lock();
    try {
      transactionStorage.clear();
      statisticsStorage.reset();
    } finally {
      lock.unlock();
    }
  }
}
