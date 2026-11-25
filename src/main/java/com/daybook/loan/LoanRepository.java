package com.daybook.loan;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("select distinct l from Loan l left join fetch l.payments where l.userId = :userId order by l.createdAt asc")
    List<Loan> findAllWithPayments(@Param("userId") Long userId);

    Optional<Loan> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("delete from Loan l where l.userId = :userId")
    void deleteAllForUser(@Param("userId") Long userId);
}
