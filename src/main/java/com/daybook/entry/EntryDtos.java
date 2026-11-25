package com.daybook.entry;

import com.daybook.common.Kind;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public final class EntryDtos {
    private EntryDtos() {}

    /** Categories and pay methods are stored as stable English keys ("Sale", "Cash"); the app translates them. */
    public record EntryRequest(
            @NotNull Kind kind,
            @NotNull @DecimalMin("0.01") @Digits(integer = 14, fraction = 2) BigDecimal amount,
            @Size(max = 120) String note,
            @NotBlank @Size(max = 30) String category,
            @NotBlank @Size(max = 20) String payMethod,
            @NotNull LocalDate date) {}

    public record EntryDto(
            Long id, Kind kind, BigDecimal amount, String note, String category,
            String payMethod, LocalDate date, Instant createdAt) {

        public static EntryDto from(Entry e) {
            return new EntryDto(e.getId(), e.getKind(), e.getAmount(), e.getNote(), e.getCategory(),
                    e.getPayMethod(), e.getEntryDate(), e.getCreatedAt());
        }
    }
}
