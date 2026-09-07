package com.chopinhhm.library.modulec;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByBookIdAndStatus(Long bookId, Reservation.Status status);
    boolean existsByBookIdAndReaderIdAndStatus(Long bookId, Long readerId, Reservation.Status status);
    List<Reservation> findByStatusOrderByCreatedAtAsc(Reservation.Status status);
    List<Reservation> findByReaderIdOrderByCreatedAtDesc(Long readerId);
}
