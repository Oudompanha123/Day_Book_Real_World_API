package com.daybook.entry;

import com.daybook.common.Kind;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "entries")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Kind kind;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 120)
    private String note = "";

    @Column(nullable = false, length = 30)
    private String category;

    @Column(name = "pay_method", nullable = false, length = 20)
    private String payMethod;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Entry() {}

    public Entry(Long userId, Kind kind, BigDecimal amount, String note, String category,
                 String payMethod, LocalDate entryDate) {
        this.userId = userId;
        this.kind = kind;
        this.amount = amount;
        this.note = note == null ? "" : note;
        this.category = category;
        this.payMethod = payMethod;
        this.entryDate = entryDate;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Kind getKind() { return kind; }
    public BigDecimal getAmount() { return amount; }
    public String getNote() { return note; }
    public String getCategory() { return category; }
    public String getPayMethod() { return payMethod; }
    public LocalDate getEntryDate() { return entryDate; }
    public Instant getCreatedAt() { return createdAt; }
}
