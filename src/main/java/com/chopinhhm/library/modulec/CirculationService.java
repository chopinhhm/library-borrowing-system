package com.chopinhhm.library.modulec;

import com.chopinhhm.library.common.BusinessException;
import com.chopinhhm.library.moduleb.Book;
import com.chopinhhm.library.moduleb.BookRepository;
import com.chopinhhm.library.moduleb.Reader;
import com.chopinhhm.library.moduleb.ReaderRepository;
import com.chopinhhm.library.moduled.BorrowingEligibilityService;
import com.chopinhhm.library.moduled.FineCalculator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CirculationService {
    private final BookRepository books;
    private final ReaderRepository readers;
    private final LoanRepository loans;
    private final ReservationRepository reservations;
    private final OverdueReminderRepository reminders;
    private final BorrowingEligibilityService eligibility;
    private final FineCalculator fineCalculator;

    public CirculationService(BookRepository books, ReaderRepository readers, LoanRepository loans,
                              ReservationRepository reservations, OverdueReminderRepository reminders,
                              BorrowingEligibilityService eligibility, FineCalculator fineCalculator) {
        this.books = books; this.readers = readers; this.loans = loans; this.reservations = reservations;
        this.reminders = reminders;
        this.eligibility = eligibility; this.fineCalculator = fineCalculator;
    }

    @Transactional
    public Loan borrow(Long readerId, Long bookId) {
        Reader reader = reader(readerId);
        Book book = book(bookId);
        eligibility.assertEligible(reader);
        if (book.getAvailableCopies() <= 0) throw new BusinessException("该图书暂无可借副本，请先预约");
        Loan loan = new Loan();
        loan.setReader(reader); loan.setBook(book); loan.setBorrowedAt(LocalDate.now());
        loan.setDueAt(LocalDate.now().plusDays(reader.getReaderType().getLoanDays()));
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        books.save(book);
        return loans.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = activeLoan(loanId);
        LocalDate returnedAt = LocalDate.now();
        loan.setReturnedAt(returnedAt);
        loan.setFine(fineCalculator.calculate(loan.getDueAt(), returnedAt, loan.getReader().getReaderType().getDailyFineRate()));
        loan.setStatus(Loan.Status.RETURNED);
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        books.save(book);
        return loans.save(loan);
    }

    @Transactional
    public Loan renew(Long loanId) {
        Loan loan = activeLoan(loanId);
        if (loan.getDueAt().isBefore(LocalDate.now())) throw new BusinessException("逾期图书不能续借，请先归还并结算罚金");
        if (loan.getRenewCount() >= loan.getReader().getReaderType().getMaxRenewals()) throw new BusinessException("已达到最大续借次数");
        if (reservations.existsByBookIdAndStatus(loan.getBook().getId(), Reservation.Status.ACTIVE)) throw new BusinessException("该图书已被预约，不能续借");
        loan.setRenewCount(loan.getRenewCount() + 1);
        loan.setDueAt(loan.getDueAt().plusDays(loan.getReader().getReaderType().getLoanDays()));
        return loans.save(loan);
    }

    @Transactional
    public Reservation reserve(Long readerId, Long bookId) {
        Reader reader = reader(readerId);
        Book book = book(bookId);
        if (reservations.existsByBookIdAndReaderIdAndStatus(bookId, readerId, Reservation.Status.ACTIVE)) throw new BusinessException("不能重复预约同一本图书");
        Reservation reservation = new Reservation();
        reservation.setReader(reader); reservation.setBook(book); reservation.setCreatedAt(LocalDateTime.now());
        return reservations.save(reservation);
    }

    public List<Loan> readerLoans(Long readerId) { return loans.findByReaderIdOrderByBorrowedAtDesc(readerId); }
    public List<Loan> allLoans(Loan.Status status, boolean overdueOnly) {
        if (overdueOnly) return overdueLoans();
        return status == null ? loans.findAll() : loans.findByStatusOrderByDueAtAsc(status);
    }
    public List<Loan> overdueLoans() { return loans.findByStatusAndDueAtBeforeOrderByDueAtAsc(Loan.Status.BORROWED, LocalDate.now()); }
    public List<OverdueReminder> reminders() { return reminders.findAllByOrderBySentAtDesc(); }
    public List<Reservation> activeReservations() { return reservations.findByStatusOrderByCreatedAtAsc(Reservation.Status.ACTIVE); }
    public List<Reservation> readerReservations(Long readerId) { return reservations.findByReaderIdOrderByCreatedAtDesc(readerId); }
    public Long readerIdForLoan(Long loanId) { return loans.findById(loanId).orElseThrow(() -> new BusinessException("借阅记录不存在")).getReader().getId(); }
    public Long readerIdForReservation(Long reservationId) { return reservations.findById(reservationId).orElseThrow(() -> new BusinessException("预约记录不存在")).getReader().getId(); }

    @Transactional
    public Reservation cancelReservation(Long reservationId) {
        Reservation reservation = reservations.findById(reservationId).orElseThrow(() -> new BusinessException("预约记录不存在"));
        if (reservation.getStatus() != Reservation.Status.ACTIVE) throw new BusinessException("该预约已结束");
        reservation.setStatus(Reservation.Status.CANCELLED);
        return reservations.save(reservation);
    }

    @Transactional
    public OverdueReminder sendReminder(Long loanId) {
        Loan loan = activeLoan(loanId);
        if (!loan.getDueAt().isBefore(LocalDate.now())) throw new BusinessException("该借阅记录尚未逾期");
        OverdueReminder reminder = new OverdueReminder();
        reminder.setLoan(loan);
        reminder.setRecipient(loan.getReader().getEmail());
        reminder.setMessage("请尽快归还《" + loan.getBook().getTitle() + "》，应还日期为 " + loan.getDueAt());
        reminder.setSentAt(LocalDateTime.now());
        return reminders.save(reminder);
    }
    private Reader reader(Long id) { return readers.findById(id).orElseThrow(() -> new BusinessException("读者不存在")); }
    private Book book(Long id) { return books.findById(id).orElseThrow(() -> new BusinessException("图书不存在")); }
    private Loan activeLoan(Long id) {
        Loan loan = loans.findById(id).orElseThrow(() -> new BusinessException("借阅记录不存在"));
        if (loan.getStatus() != Loan.Status.BORROWED) throw new BusinessException("该借阅记录已归还");
        return loan;
    }
}
