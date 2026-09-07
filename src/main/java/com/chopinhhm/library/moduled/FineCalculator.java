package com.chopinhhm.library.moduled;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class FineCalculator {
    public BigDecimal calculate(LocalDate dueAt, LocalDate returnedAt, BigDecimal dailyRate) {
        if (dueAt == null || returnedAt == null || !returnedAt.isAfter(dueAt)) return BigDecimal.ZERO;
        return dailyRate.multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(dueAt, returnedAt)));
    }
}
