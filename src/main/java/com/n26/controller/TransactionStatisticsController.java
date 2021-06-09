package com.n26.controller;

import static com.n26.utils.Validation.isTransactionFromTheFuture;
import static com.n26.utils.Validation.isTransactionOlder60Seconds;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.n26.domain.Stats;
import com.n26.domain.Transaction;
import com.n26.service.TransactionStatisticsService;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionStatisticsController {

  private final TransactionStatisticsService service;

  @PostMapping("/transactions")
  public ResponseEntity<?> postTransaction(@RequestBody Transaction transaction) {
    final Instant now = Instant.now();
    if (isTransactionOlder60Seconds(transaction, now)) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else if (isTransactionFromTheFuture(transaction, now)) {
      return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }
    service.addTransactionAndUpdateStat(transaction);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @ExceptionHandler({InvalidFormatException.class, DateTimeParseException.class})
  public ResponseEntity<?> onNoParsableAttributes() {
    return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @DeleteMapping("/transactions")
  public ResponseEntity<?> delete() {
    service.clearTransactionsAndResetStat();
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/statistics")
  public ResponseEntity<Stats> stat() {
    return ResponseEntity.ok(service.getStatistics());
  }
}
