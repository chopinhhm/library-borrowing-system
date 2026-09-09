package com.chopinhhm.library.modulec;

import java.util.List;
import com.chopinhhm.library.modulea.AccessGuard;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/circulation")
public class CirculationController {
    private final CirculationService service;
    private final AccessGuard accessGuard;
    public CirculationController(CirculationService service, AccessGuard accessGuard) { this.service = service; this.accessGuard = accessGuard; }
    @PostMapping("/borrow") public Loan borrow(@RequestParam Long readerId, @RequestParam Long bookId) { return service.borrow(readerId, bookId); }
    @PostMapping("/loans/{loanId}/return") public Loan returnBook(@PathVariable Long loanId) { return service.returnBook(loanId); }
    @PostMapping("/loans/{loanId}/renew") public Loan renew(@PathVariable Long loanId) { accessGuard.assertReaderAccess(service.readerIdForLoan(loanId)); return service.renew(loanId); }
    @PostMapping("/reserve") public Reservation reserve(@RequestParam Long readerId, @RequestParam Long bookId) { accessGuard.assertReaderAccess(readerId); return service.reserve(readerId, bookId); }
    @GetMapping("/readers/{readerId}/loans") public List<Loan> readerLoans(@PathVariable Long readerId) { accessGuard.assertReaderAccess(readerId); return service.readerLoans(readerId); }
    @GetMapping("/readers/{readerId}/reservations") public List<Reservation> readerReservations(@PathVariable Long readerId) { accessGuard.assertReaderAccess(readerId); return service.readerReservations(readerId); }
    @PostMapping("/reservations/{reservationId}/cancel") public Reservation cancel(@PathVariable Long reservationId) { accessGuard.assertReaderAccess(service.readerIdForReservation(reservationId)); return service.cancelReservation(reservationId); }
    @GetMapping("/admin/loans")
    public List<Loan> allLoans(@RequestParam(required = false) Loan.Status status,
                               @RequestParam(defaultValue = "false") boolean overdueOnly) {
        return service.allLoans(status, overdueOnly);
    }
    @GetMapping("/admin/overdue") public List<Loan> overdue() { return service.overdueLoans(); }
    @GetMapping("/admin/reminders") public List<OverdueReminder> reminders() { return service.reminders(); }
    @PostMapping("/admin/loans/{loanId}/remind") public OverdueReminder remind(@PathVariable Long loanId) { return service.sendReminder(loanId); }
    @GetMapping("/admin/reservations") public List<Reservation> reservations() { return service.activeReservations(); }
}
