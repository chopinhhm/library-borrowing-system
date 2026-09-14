package com.chopinhhm.library.moduleb;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
    boolean existsByIsbn(String isbn);
    Optional<Book> findByIsbn(String isbn);
}
