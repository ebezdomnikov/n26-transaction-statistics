package com.n26.storage;

import com.n26.domain.Stats;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

@Component
public class StatisticsStorage {

  private final AtomicReference<DoubleSummaryStatistics> storage =
      new AtomicReference<>(new DoubleSummaryStatistics());

  public void reset() {
    storage.set(new DoubleSummaryStatistics());
  }

  public Stats getStatistics() {
    return new Stats(storage.get());
  }

  public void update(double value) {
    storage.get().accept(value);
  }

  public void update(DoubleSummaryStatistics other) {
    storage.get().combine(other);
  }
}
