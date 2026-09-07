package com.chopinhhm.library.modulec;

import java.util.List;
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
    public CirculationController(CirculationService service) { this.service = service; }
    @PostMapping("/borrow") public Loan borrow(@RequestParam Long readerId, @RequestParam Long bookId) { return service.borrow(readerId, bookId); }
    @PostMapping("/loans/{loanId}/return") public Loan returnBook(@PathVariable Long loanId) { return service.returnBook(loanId); }
    @PostMapping("/loans/{loanId}/renew") public Loan renew(@PathVariable Long loanId) { return service.renew(loanId); }
    @PostMapping("/reserve") public Reservation reserve(@RequestParam Long readerId, @RequestParam Long bookId) { return service.reserve(readerId, bookId); }
    @GetMapping("/readers/{readerId}/loans") public List<Loan> readerLoans(@PathVariable Long readerId) { return service.readerLoans(readerId); }
}
