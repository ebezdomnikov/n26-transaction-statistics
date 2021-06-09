package com.n26.domain;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.DoubleSummaryStatistics;
import org.junit.Test;

public class StatsTest {

  @Test
  public void canCreateJsonWithCorrectNumberFormat() throws JsonProcessingException {
    DoubleSummaryStatistics summaryStatistics = new DoubleSummaryStatistics();
    summaryStatistics.accept(10.15);
    summaryStatistics.accept(4.7);
    summaryStatistics.accept(12.40);
    Stats stats = new Stats(summaryStatistics);
    ObjectMapper objectMapper = new ObjectMapper();
    final String asJson = objectMapper.writeValueAsString(stats);
    assertEquals(
        "{\"sum\":\"27.25\",\"avg\":\"9.08\",\"max\":\"12.40\",\"min\":\"4.70\",\"count\":3}",
        asJson);
  }

  @Test
  public void canCreateJsonForEmptyStat() throws JsonProcessingException {
    DoubleSummaryStatistics summaryStatistics = new DoubleSummaryStatistics();
    Stats stats = new Stats(summaryStatistics);
    ObjectMapper objectMapper = new ObjectMapper();
    final String asJson = objectMapper.writeValueAsString(stats);
    assertEquals(
        "{\"sum\":\"0.00\",\"avg\":\"0.00\",\"max\":\"0.00\",\"min\":\"0.00\",\"count\":0}",
        asJson);
  }
}
