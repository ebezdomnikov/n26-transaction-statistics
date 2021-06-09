package com.n26.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.DoubleSummaryStatistics;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Dto for statistics.
 *
 * <p>Have some JsonFormat annotations for proper transformation into the response format.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Stats {

  @JsonFormat(shape = Shape.STRING)
  private BigDecimal sum;

  @JsonFormat(shape = Shape.STRING)
  private BigDecimal avg;

  @JsonFormat(shape = Shape.STRING)
  private BigDecimal max;

  @JsonFormat(shape = Shape.STRING)
  private BigDecimal min;

  private Long count;

  public Stats(DoubleSummaryStatistics summaryStatistics) {
    this.avg = format(summaryStatistics.getAverage());
    this.sum = format(summaryStatistics.getSum());
    this.max = format(summaryStatistics.getMax());
    this.min = format(summaryStatistics.getMin());
    this.count = summaryStatistics.getCount();
  }

  private static BigDecimal format(final double val) {
    if (val == Double.NEGATIVE_INFINITY || val == Double.POSITIVE_INFINITY) {
      return BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP);
    }
    return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
  }
}
