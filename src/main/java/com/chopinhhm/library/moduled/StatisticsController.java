package com.chopinhhm.library.moduled;

import com.chopinhhm.library.modulea.OperationLogRepository;
import com.chopinhhm.library.moduleb.BookRepository;
import com.chopinhhm.library.moduleb.ReaderRepository;
import com.chopinhhm.library.modulec.Loan;
import com.chopinhhm.library.modulec.LoanRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {
    private final BookRepository books;
    private final ReaderRepository readers;
    private final LoanRepository loans;
    private final OperationLogRepository logs;
    public StatisticsController(BookRepository books, ReaderRepository readers, LoanRepository loans, OperationLogRepository logs) {
        this.books = books; this.readers = readers; this.loans = loans; this.logs = logs;
    }
    @GetMapping
    public Map<String, Long> overview() {
        Map<String, Long> result = new LinkedHashMap<String, Long>();
        result.put("bookTitles", books.count());
        result.put("readers", readers.count());
        result.put("activeLoans", loans.countByStatus(Loan.Status.BORROWED));
        result.put("returnedLoans", loans.countByStatus(Loan.Status.RETURNED));
        result.put("operationLogs", logs.count());
        return result;
    }
}
