package com.chopinhhm.library.moduled;

import com.chopinhhm.library.common.BusinessException;
import com.chopinhhm.library.moduleb.Reader;
import com.chopinhhm.library.modulec.Loan;
import com.chopinhhm.library.modulec.LoanRepository;
import org.springframework.stereotype.Service;

@Service
public class BorrowingEligibilityService {
    private final LoanRepository loans;
    public BorrowingEligibilityService(LoanRepository loans) { this.loans = loans; }

    public void assertEligible(Reader reader) {
        if (reader.getStatus() != Reader.Status.ACTIVE) throw new BusinessException("读者账号已暂停借阅");
        long activeLoans = loans.countByReaderIdAndStatus(reader.getId(), Loan.Status.BORROWED);
        if (activeLoans >= reader.getReaderType().getMaxBooks()) throw new BusinessException("已达到最大借阅数量");
    }
}
