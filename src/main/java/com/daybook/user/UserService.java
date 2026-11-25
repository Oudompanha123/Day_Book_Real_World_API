package com.daybook.user;

import com.daybook.common.ApiException;
import com.daybook.entry.EntryRepository;
import com.daybook.loan.LoanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository users;
    private final EntryRepository entries;
    private final LoanRepository loans;

    public UserService(UserRepository users, EntryRepository entries, LoanRepository loans) {
        this.users = users;
        this.entries = entries;
        this.loans = loans;
    }

    /** A valid token for a deleted account is treated as signed out. */
    public User require(long userId) {
        return users.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "unauthorized"));
    }

    @Transactional(readOnly = true)
    public MeDto me(long userId) {
        return MeDto.from(require(userId));
    }

    @Transactional
    public MeDto updateSettings(long userId, SettingsRequest req) {
        User u = require(userId);
        u.setBusinessName(req.businessName() == null ? "" : req.businessName().trim());
        u.setCurrency(req.currency());
        u.setLanguage(req.language());
        return MeDto.from(u);
    }

    /** "Start over": removes every entry and loan and clears the business name. Keeps the account. */
    @Transactional
    public MeDto eraseData(long userId) {
        User u = require(userId);
        loans.deleteAllForUser(userId);   // payments go with them (ON DELETE CASCADE)
        entries.deleteAllForUser(userId);
        u.setBusinessName("");
        return MeDto.from(u);
    }
}
