package com.chopinhhm.library.moduleb;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReaderTypeRepository extends JpaRepository<ReaderType, Long> {
    Optional<ReaderType> findByName(String name);
}
