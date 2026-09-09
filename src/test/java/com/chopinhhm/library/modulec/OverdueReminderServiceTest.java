package com.chopinhhm.library.modulec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chopinhhm.library.moduleb.Book;
import com.chopinhhm.library.moduleb.BookRepository;
import com.chopinhhm.library.moduleb.Reader;
import com.chopinhhm.library.moduleb.ReaderRepository;
import com.chopinhhm.library.moduled.BorrowingEligibilityService;
import com.chopinhhm.library.moduled.FineCalculator;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OverdueReminderServiceTest {
    @Mock private BookRepository books;
    @Mock private ReaderRepository readers;
    @Mock private LoanRepository loans;
    @Mock private ReservationRepository reservations;
    @Mock private OverdueReminderRepository reminders;
    @Mock private BorrowingEligibilityService eligibility;
    @Mock private FineCalculator fineCalculator;

    @Test
    void createsTraceableReminderForOverdueLoan() {
        Reader reader = new Reader();
        reader.setName("测试读者");
        reader.setEmail("reader@example.com");
        Book book = new Book();
        book.setTitle("软件工程");
        Loan loan = new Loan();
        loan.setReader(reader);
        loan.setBook(book);
        loan.setDueAt(LocalDate.now().minusDays(2));
        when(loans.findById(7L)).thenReturn(Optional.of(loan));
        when(reminders.save(any(OverdueReminder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CirculationService service = new CirculationService(books, readers, loans, reservations,
            reminders, eligibility, fineCalculator);
        OverdueReminder result = service.sendReminder(7L);

        ArgumentCaptor<OverdueReminder> saved = ArgumentCaptor.forClass(OverdueReminder.class);
        verify(reminders).save(saved.capture());
        assertSame(loan, result.getLoan());
        assertEquals("reader@example.com", saved.getValue().getRecipient());
        assertEquals(OverdueReminder.Channel.SYSTEM, saved.getValue().getChannel());
    }
}
