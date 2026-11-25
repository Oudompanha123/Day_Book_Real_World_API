package com.daybook.loan;

import com.daybook.common.Kind;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public final class LoanDtos {
    private LoanDtos() {}

    public record LoanRequest(
            @NotNull Kind direction,
            @NotBlank @Size(max = 60) String name,
            @NotNull @DecimalMin("0.01") @Digits(integer = 14, fraction = 2) BigDecimal amount,
            @Size(max = 120) String note,
            @NotNull LocalDate lentOn,
            LocalDate dueOn) {}

    /**
     * @param countAsEntry also record the payment as income (they paid me) or an expense (I paid them)
     * @param entryNote    note for that entry, already in the user's language (e.g. "Repayment from Dara")
     */
    public record PaymentRequest(
            @NotNull @DecimalMin("0.01") @Digits(integer = 14, fraction = 2) BigDecimal amount,
            @NotNull LocalDate paidOn,
            boolean countAsEntry,
            @Size(max = 120) String entryNote) {}

    public record PaymentDto(Long id, BigDecimal amount, LocalDate paidOn, Long entryId, Instant createdAt) {
        static PaymentDto from(LoanPayment p) {
            return new PaymentDto(p.getId(), p.getAmount(), p.getPaidOn(), p.getEntryId(), p.getCreatedAt());
        }
    }

    public record LoanDto(
            Long id, Kind direction, String name, BigDecimal amount, String note,
            LocalDate lentOn, LocalDate dueOn, boolean paid, Instant paidAt, Instant createdAt,
            BigDecimal paidSoFar, BigDecimal remaining, List<PaymentDto> payments) {

        public static LoanDto from(Loan l) {
            List<PaymentDto> pays = l.getPayments().stream()
                    .sorted(Comparator.comparing(LoanPayment::getPaidOn).thenComparing(LoanPayment::getCreatedAt))
                    .map(PaymentDto::from)
                    .toList();
            return new LoanDto(l.getId(), l.getDirection(), l.getName(), l.getAmount(), l.getNote(),
                    l.getLentOn(), l.getDueOn(), l.isPaid(), l.getPaidAt(), l.getCreatedAt(),
                    l.paidSoFar(), l.remaining(), pays);
        }
    }
}
