package com.n26.storage;

import static org.junit.Assert.assertEquals;

import com.n26.domain.Stats;
import org.junit.Before;
import org.junit.Test;

public class StatisticsStorageTest {

  private StatisticsStorage sut;

  @Before
  public void setUp() throws Exception {
    sut = new StatisticsStorage();
    sut.reset();
  }

  @Test
  public void canReset() {

    sut.update(10L);
    sut.update(20L);
    sut.update(30L);

    final Stats statBefore = sut.getStatistics();
    assertEquals(3L, statBefore.getCount().longValue());
    assertEquals(30L, statBefore.getMax().longValue());
    assertEquals(10L, statBefore.getMin().longValue());
    assertEquals(60L, statBefore.getSum().longValue());
    assertEquals(20L, statBefore.getAvg().longValue());

    sut.reset();

    final Stats statAfter = sut.getStatistics();
    assertEquals(0L, statAfter.getCount().longValue());
    assertEquals(0L, statAfter.getMax().longValue());
    assertEquals(0L, statAfter.getMin().longValue());
    assertEquals(0L, statAfter.getSum().longValue());
    assertEquals(0L, statAfter.getAvg().longValue());
  }

  @Test
  public void canUpdateStatistics() {
    sut.update(10L);
    sut.update(20L);
    sut.update(30L);
    final Stats actual = sut.getStatistics();
    assertEquals(3L, actual.getCount().longValue());
    assertEquals(30L, actual.getMax().longValue());
    assertEquals(10L, actual.getMin().longValue());
    assertEquals(60L, actual.getSum().longValue());
    assertEquals(20L, actual.getAvg().longValue());
  }
}
