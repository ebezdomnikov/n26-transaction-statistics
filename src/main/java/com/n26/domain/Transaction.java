package com.n26.domain;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

  private BigDecimal amount;
  private Instant timestamp;

  public static Transaction create(final double amount, final Instant timestamp) {
    final Transaction transaction = new Transaction();
    transaction.setAmount(BigDecimal.valueOf(amount));
    transaction.setTimestamp(timestamp);
    return transaction;
  }
}
