package com.chopinhhm.library.moduled;

import com.chopinhhm.library.modulec.Loan;
import com.chopinhhm.library.modulec.LoanRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OverdueAnalysisService {
    private final LoanRepository loans;
    private final FineCalculator fineCalculator;

    public OverdueAnalysisService(LoanRepository loans, FineCalculator fineCalculator) {
        this.loans = loans;
        this.fineCalculator = fineCalculator;
    }

    public List<OverdueAnalysis> currentReport() { return reportAt(LocalDate.now()); }

    List<OverdueAnalysis> reportAt(LocalDate reportDate) {
        List<Loan> overdueLoans = loans.findByStatusAndDueAtBeforeOrderByDueAtAsc(Loan.Status.BORROWED, reportDate);
        List<OverdueAnalysis> report = new ArrayList<OverdueAnalysis>();
        for (Loan loan : overdueLoans) {
            BigDecimal fine = fineCalculator.calculate(loan.getDueAt(), reportDate,
                loan.getReader().getReaderType().getDailyFineRate());
            report.add(new OverdueAnalysis(loan.getId(), loan.getReader().getName(), loan.getBook().getTitle(),
                loan.getDueAt(), ChronoUnit.DAYS.between(loan.getDueAt(), reportDate), fine));
        }
        return report;
    }
}
