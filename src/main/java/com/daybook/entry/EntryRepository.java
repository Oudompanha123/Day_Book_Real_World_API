package com.daybook.entry;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    List<Entry> findByUserIdAndEntryDateBetweenOrderByEntryDateDescCreatedAtDesc(
            Long userId, LocalDate from, LocalDate to);

    List<Entry> findByUserIdOrderByEntryDateAscCreatedAtAsc(Long userId);

    Optional<Entry> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("delete from Entry e where e.userId = :userId")
    void deleteAllForUser(@Param("userId") Long userId);
}
