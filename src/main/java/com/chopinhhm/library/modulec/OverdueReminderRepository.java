package com.chopinhhm.library.modulec;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OverdueReminderRepository extends JpaRepository<OverdueReminder, Long> {
    List<OverdueReminder> findAllByOrderBySentAtDesc();
}
