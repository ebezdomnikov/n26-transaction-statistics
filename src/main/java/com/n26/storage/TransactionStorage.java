package com.n26.storage;

import com.n26.domain.Transaction;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.collections4.map.PassiveExpiringMap.ExpirationPolicy;
import org.springframework.stereotype.Component;

@Component
public class TransactionStorage {

  private final AtomicLong id = new AtomicLong();
  // NOTE: PassiveExpiringMap not thread-safe, so the Lock class will take care about it
  // PassiveExpiringMap impl applies ExpirationPolicy in order to keep only live transactions
  private final Map<Long, Transaction> transactions =
      new PassiveExpiringMap<>(
          (ExpirationPolicy<Long, Transaction>)
              (key, value) ->
                  // here need to truncated to seconds, as we have granularity of statistics = 1 sec
                  // + 60_000 as we have definition of live transaction: not older 60 sec.
                  value.getTimestamp().truncatedTo(ChronoUnit.SECONDS).toEpochMilli() + 60_000);

  public Collection<Transaction> getLiveTransactions() {
    return transactions.values();
  }

  public void addTransaction(final Transaction transaction) {
    transactions.put(id.getAndIncrement(), transaction);
  }

  public void clear() {
    transactions.clear();
  }
}
