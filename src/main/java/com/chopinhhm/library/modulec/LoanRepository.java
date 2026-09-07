package com.chopinhhm.library.modulec;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    long countByReaderIdAndStatus(Long readerId, Loan.Status status);
    long countByStatus(Loan.Status status);
    List<Loan> findByReaderIdOrderByBorrowedAtDesc(Long readerId);
}
