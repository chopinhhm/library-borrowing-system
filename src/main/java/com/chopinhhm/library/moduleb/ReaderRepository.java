package com.chopinhhm.library.moduleb;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
    boolean existsByCardNumber(String cardNumber);
    Optional<Reader> findByCardNumber(String cardNumber);
}
