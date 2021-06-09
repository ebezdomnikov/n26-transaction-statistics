package com.n26.utils;

import com.n26.domain.Transaction;
import java.time.Duration;
import java.time.Instant;

public final class Validation {

  private Validation() {
    //
  }

  public static boolean isTransactionOlder60Seconds(
      final Transaction transaction, final Instant now) {
    return Duration.between(transaction.getTimestamp(), now).getSeconds() >= 60;
  }

  public static boolean isTransactionFromTheFuture(
      final Transaction transaction, final Instant now) {
    return transaction.getTimestamp().isAfter(now);
  }
}
