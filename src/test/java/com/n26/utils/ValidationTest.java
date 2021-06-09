package com.n26.utils;

import static com.n26.utils.Validation.isTransactionFromTheFuture;
import static com.n26.utils.Validation.isTransactionOlder60Seconds;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.n26.domain.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.Test;

public class ValidationTest {

  @Test
  public void canCheckIfTransactionIsOlder60Seconds() {
    Transaction transaction = new Transaction();
    transaction.setAmount(BigDecimal.valueOf(10.50));
    transaction.setTimestamp(Instant.now().minusSeconds(61));
    assertTrue(isTransactionOlder60Seconds(transaction, Instant.now()));
  }

  @Test
  public void canCheckIfTransactionIsNotOlder60seconds() {
    Transaction transaction = new Transaction();
    transaction.setAmount(BigDecimal.valueOf(10.50));
    transaction.setTimestamp(Instant.now().minusSeconds(10));
    assertFalse(isTransactionOlder60Seconds(transaction, Instant.now()));
  }

  @Test
  public void canCheckIfTransactionFromTheFuture() {
    Transaction transaction = new Transaction();
    transaction.setAmount(BigDecimal.valueOf(10.50));
    transaction.setTimestamp(Instant.now().plusSeconds(10));
    assertTrue(isTransactionFromTheFuture(transaction, Instant.now()));
  }

  @Test
  public void canCheckIfTransactionNotFromTheFuture() {
    Transaction transaction = new Transaction();
    transaction.setAmount(BigDecimal.valueOf(10.50));
    transaction.setTimestamp(Instant.now().minusSeconds(10));
    assertFalse(isTransactionFromTheFuture(transaction, Instant.now()));
  }
}
