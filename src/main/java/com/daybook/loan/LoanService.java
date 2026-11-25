package com.daybook.loan;

import com.daybook.common.ApiException;
import com.daybook.common.Dates;
import com.daybook.common.Kind;
import com.daybook.entry.Entry;
import com.daybook.entry.EntryRepository;
import com.daybook.loan.LoanDtos.LoanDto;
import com.daybook.loan.LoanDtos.LoanRequest;
import com.daybook.loan.LoanDtos.PaymentRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {

    private final LoanRepository loans;
    private final EntryRepository entries;

    public LoanService(LoanRepository loans, EntryRepository entries) {
        this.loans = loans;
        this.entries = entries;
    }

    @Transactional(readOnly = true)
    public List<LoanDto> list(long userId) {
        return loans.findAllWithPayments(userId).stream().map(LoanDto::from).toList();
    }

    @Transactional(readOnly = true)
    public LoanDto get(long userId, long id) {
        return LoanDto.from(find(userId, id));
    }

    @Transactional
    public LoanDto create(long userId, LoanRequest req) {
        validateDates(req);
        Loan loan = new Loan(userId);
        apply(loan, req);
        return LoanDto.from(loans.save(loan));
    }

    @Transactional
    public LoanDto update(long userId, long id, LoanRequest req) {
        validateDates(req);
        Loan loan = find(userId, id);
        if (req.amount().compareTo(loan.paidSoFar()) < 0) {
            throw ApiException.badRequest("amount_below_paid");
        }
        apply(loan, req);
        loan.refreshPaidStatus();
        return LoanDto.from(loan);
    }

    @Transactional
    public void delete(long userId, long id) {
        loans.delete(find(userId, id));
    }

    @Transactional
    public LoanDto addPayment(long userId, long id, PaymentRequest req) {
        Loan loan = find(userId, id);
        if (loan.isPaid()) throw ApiException.badRequest("already_settled");
        if (req.amount().compareTo(loan.remaining()) > 0) throw ApiException.badRequest("amount_too_high");
        if (Dates.isFuture(req.paidOn())) throw ApiException.badRequest("future_date");

        Long entryId = null;
        if (req.countAsEntry()) {
            // Money coming back to me is income; money I pay back is an expense.
            Kind kind = loan.getDirection() == Kind.IN ? Kind.IN : Kind.OUT;
            String note = req.entryNote() == null || req.entryNote().isBlank()
                    ? loan.getName() : req.entryNote().trim();
            Entry entry = entries.save(new Entry(userId, kind, req.amount(), note, "Other", "Cash", req.paidOn()));
            entryId = entry.getId();
        }
        loan.getPayments().add(new LoanPayment(loan, req.amount(), req.paidOn(), entryId));
        loan.refreshPaidStatus();
        loans.flush();
        return LoanDto.from(loan);
    }

    /** Removes a payment, and the income/expense entry that was recorded with it. */
    @Transactional
    public LoanDto deletePayment(long userId, long loanId, long paymentId) {
        Loan loan = find(userId, loanId);
        LoanPayment payment = loan.getPayments().stream()
                .filter(p -> p.getId().equals(paymentId))
                .findFirst()
                .orElseThrow(ApiException::notFound);
        loan.getPayments().remove(payment);
        if (payment.getEntryId() != null) {
            entries.findByIdAndUserId(payment.getEntryId(), userId).ifPresent(entries::delete);
        }
        loan.refreshPaidStatus();
        return LoanDto.from(loan);
    }

    private Loan find(long userId, long id) {
        return loans.findByIdAndUserId(id, userId).orElseThrow(ApiException::notFound);
    }

    private static void validateDates(LoanRequest req) {
        if (req.dueOn() != null && req.dueOn().isBefore(req.lentOn())) {
            throw ApiException.badRequest("due_before_loan");
        }
        if (Dates.isFuture(req.lentOn())) throw ApiException.badRequest("future_date");
    }

    private static void apply(Loan loan, LoanRequest req) {
        loan.setDirection(req.direction());
        loan.setName(req.name().trim());
        loan.setAmount(req.amount());
        loan.setNote(req.note() == null ? "" : req.note().trim());
        loan.setLentOn(req.lentOn());
        loan.setDueOn(req.dueOn());
    }
}
