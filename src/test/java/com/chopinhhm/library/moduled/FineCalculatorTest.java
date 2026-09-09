package com.chopinhhm.library.moduled;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class FineCalculatorTest {
    private final FineCalculator calculator = new FineCalculator();

    @Test
    void returnsZeroWhenReturnedOnTime() {
        BigDecimal result = calculator.calculate(LocalDate.of(2026, 9, 7), LocalDate.of(2026, 9, 7), new BigDecimal("0.50"));
        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    void calculatesFineByOverdueDays() {
        BigDecimal result = calculator.calculate(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5), new BigDecimal("0.50"));
        assertThat(result).isEqualByComparingTo("2.00");
    }
}
