package com.chopinhhm.library.moduled;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OverdueAnalysis {
    private final Long loanId;
    private final String readerName;
    private final String bookTitle;
    private final LocalDate dueAt;
    private final long overdueDays;
    private final BigDecimal estimatedFine;

    public OverdueAnalysis(Long loanId, String readerName, String bookTitle, LocalDate dueAt,
                           long overdueDays, BigDecimal estimatedFine) {
        this.loanId = loanId;
        this.readerName = readerName;
        this.bookTitle = bookTitle;
        this.dueAt = dueAt;
        this.overdueDays = overdueDays;
        this.estimatedFine = estimatedFine;
    }

    public Long getLoanId() { return loanId; }
    public String getReaderName() { return readerName; }
    public String getBookTitle() { return bookTitle; }
    public LocalDate getDueAt() { return dueAt; }
    public long getOverdueDays() { return overdueDays; }
    public BigDecimal getEstimatedFine() { return estimatedFine; }
}
