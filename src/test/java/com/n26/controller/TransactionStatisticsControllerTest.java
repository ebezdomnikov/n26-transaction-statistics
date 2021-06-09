package com.n26.controller;

import static com.n26.domain.Transaction.create;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.domain.Stats;
import com.n26.domain.Transaction;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionStatisticsControllerTest {

  @Autowired private TestRestTemplate testRestTemplate;

  @Test
  public void canPostTransaction() {
    Transaction transaction = create(200.00, Instant.now());
    ResponseEntity<Void> response =
        testRestTemplate.postForEntity("/transactions", transaction, Void.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  public void canReturnNoContentIfTransactionIsOlder60Seconds() {
    Transaction transaction = create(200.00, Instant.now().minus(120, ChronoUnit.SECONDS));
    ResponseEntity<Void> response =
        testRestTemplate.postForEntity("/transactions", transaction, Void.class);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  public void canTriggerDeleteTransaction() {
    ResponseEntity<Void> response =
        testRestTemplate.exchange("/transactions", HttpMethod.DELETE, null, Void.class);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  public void canReturnBadRequestIfNotJsonHasBeenSent() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    ResponseEntity<Void> response =
        testRestTemplate.exchange(
            "/transactions", HttpMethod.POST, new HttpEntity<>("Test", headers), Void.class);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void canReturnCurrentStatistics() throws JsonProcessingException {

    testRestTemplate.delete("/transactions");

    DoubleSummaryStatistics summaryStatistics = new DoubleSummaryStatistics();
    summaryStatistics.accept(10.15);
    summaryStatistics.accept(20.45);
    summaryStatistics.accept(30.99);

    ObjectMapper objectMapper = new ObjectMapper();
    String expected = objectMapper.writeValueAsString(new Stats(summaryStatistics));

    testRestTemplate.postForEntity("/transactions", create(10.15, Instant.now()), Void.class);
    testRestTemplate.postForEntity("/transactions", create(20.45, Instant.now()), Void.class);
    testRestTemplate.postForEntity("/transactions", create(30.99, Instant.now()), Void.class);

    ResponseEntity<Stats> response = testRestTemplate.getForEntity("/statistics", Stats.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expected, objectMapper.writeValueAsString(response.getBody()));
  }
}
