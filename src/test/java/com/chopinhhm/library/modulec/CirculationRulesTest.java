package com.chopinhhm.library.modulec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.chopinhhm.library.common.BusinessException;
import com.chopinhhm.library.moduleb.BookRepository;
import com.chopinhhm.library.moduleb.ReaderRepository;
import com.chopinhhm.library.moduled.BorrowingEligibilityService;
import com.chopinhhm.library.moduled.FineCalculator;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CirculationRulesTest {
    @Mock private BookRepository books;
    @Mock private ReaderRepository readers;
    @Mock private LoanRepository loans;
    @Mock private ReservationRepository reservations;
    @Mock private OverdueReminderRepository reminders;
    @Mock private BorrowingEligibilityService eligibility;
    @Mock private FineCalculator fineCalculator;

    @Test
    void refusesToRenewAnOverdueLoan() {
        Loan loan = new Loan();
        loan.setDueAt(LocalDate.now().minusDays(1));
        when(loans.findById(5L)).thenReturn(Optional.of(loan));
        CirculationService service = service();

        BusinessException error = assertThrows(BusinessException.class, () -> service.renew(5L));

        assertEquals("逾期图书不能续借，请先归还并结算罚金", error.getMessage());
    }

    @Test
    void filtersAdministrativeLoansByStatus() {
        when(loans.findByStatusOrderByDueAtAsc(Loan.Status.RETURNED)).thenReturn(Collections.<Loan>emptyList());
        assertEquals(0, service().allLoans(Loan.Status.RETURNED, false).size());
    }

    private CirculationService service() {
        return new CirculationService(books, readers, loans, reservations, reminders, eligibility, fineCalculator);
    }
}
