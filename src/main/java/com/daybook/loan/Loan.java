package com.daybook.loan;

import com.daybook.common.Kind;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** IN = they owe me, OUT = I owe. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Kind direction;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 120)
    private String note = "";

    @Column(name = "lent_on", nullable = false)
    private LocalDate lentOn;

    @Column(name = "due_on")
    private LocalDate dueOn;

    @Column(nullable = false)
    private boolean paid;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("paidOn ASC, createdAt ASC")
    private List<LoanPayment> payments = new ArrayList<>();

    protected Loan() {}

    public Loan(Long userId) {
        this.userId = userId;
    }

    public BigDecimal paidSoFar() {
        return payments.stream().map(LoanPayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal remaining() {
        BigDecimal r = amount.subtract(paidSoFar());
        return r.signum() < 0 ? BigDecimal.ZERO : r;
    }

    /** Marks the loan settled once payments cover the amount, and reopens it if they no longer do. */
    public void refreshPaidStatus() {
        boolean settled = remaining().signum() == 0;
        if (settled && !paid) {
            paid = true;
            paidAt = Instant.now();
        } else if (!settled) {
            paid = false;
            paidAt = null;
        }
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Kind getDirection() { return direction; }
    public void setDirection(Kind direction) { this.direction = direction; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note == null ? "" : note; }
    public LocalDate getLentOn() { return lentOn; }
    public void setLentOn(LocalDate lentOn) { this.lentOn = lentOn; }
    public LocalDate getDueOn() { return dueOn; }
    public void setDueOn(LocalDate dueOn) { this.dueOn = dueOn; }
    public boolean isPaid() { return paid; }
    public Instant getPaidAt() { return paidAt; }
    public Instant getCreatedAt() { return createdAt; }
    public List<LoanPayment> getPayments() { return payments; }
}
