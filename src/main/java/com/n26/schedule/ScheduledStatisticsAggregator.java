package com.n26.schedule;

import com.n26.lock.Lock;
import com.n26.storage.StatisticsStorage;
import com.n26.storage.TransactionStorage;
import java.util.DoubleSummaryStatistics;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledStatisticsAggregator {

  private final Lock lock;
  private final StatisticsStorage statisticsStorage;
  private final TransactionStorage transactionStorage;

  @PostConstruct
  public void init() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.initialize();
    // here you cron just to be sure what we trigger stats update exactly every second
    scheduler.schedule(this::aggregate, new CronTrigger("* * * * * *"));
    log.debug("scheduler initialized");
  }

  void aggregate() {
    // lock the system
    lock.lock();
    try {
      log.debug("aggregate statistics started");
      // create new instance for summary data
      final DoubleSummaryStatistics newStat = new DoubleSummaryStatistics();
      // get live transactions (transactionStorage take care about clean-up olds ones)
      transactionStorage
          .getLiveTransactions()
          .forEach(it -> newStat.accept(it.getAmount().doubleValue()));
      // reset prev stats
      statisticsStorage.reset();
      // update stats
      statisticsStorage.update(newStat);
      log.debug("statistics updated: {}", newStat);
    } catch (Exception exc) {
      log.error("Error occurred during statistics aggregation", exc);
    } finally {
      // unlock the system
      lock.unlock();
      log.debug("aggregate statistics finished");
    }
  }
}
