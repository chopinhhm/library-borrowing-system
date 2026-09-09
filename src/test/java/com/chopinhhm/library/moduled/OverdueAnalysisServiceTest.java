package com.chopinhhm.library.moduled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.chopinhhm.library.moduleb.Book;
import com.chopinhhm.library.moduleb.Reader;
import com.chopinhhm.library.moduleb.ReaderType;
import com.chopinhhm.library.modulec.Loan;
import com.chopinhhm.library.modulec.LoanRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OverdueAnalysisServiceTest {
    @Mock private LoanRepository loans;

    @Test
    void calculatesDaysAndEstimatedFineForCurrentOverdueLoans() {
        LocalDate reportDate = LocalDate.of(2026, 9, 7);
        ReaderType type = new ReaderType();
        type.setDailyFineRate(new BigDecimal("0.50"));
        Reader reader = new Reader();
        reader.setName("测试读者");
        reader.setReaderType(type);
        Book book = new Book();
        book.setTitle("软件工程");
        Loan loan = new Loan();
        loan.setReader(reader);
        loan.setBook(book);
        loan.setDueAt(reportDate.minusDays(3));
        when(loans.findByStatusAndDueAtBeforeOrderByDueAtAsc(Loan.Status.BORROWED, reportDate))
            .thenReturn(Collections.singletonList(loan));

        List<OverdueAnalysis> result = new OverdueAnalysisService(loans, new FineCalculator()).reportAt(reportDate);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getOverdueDays());
        assertEquals(new BigDecimal("1.50"), result.get(0).getEstimatedFine());
    }
}
