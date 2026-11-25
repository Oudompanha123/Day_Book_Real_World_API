package com.daybook.auth;

import com.daybook.common.ApiException;
import com.daybook.user.User;
import com.daybook.user.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    /** BCrypt only uses the first 72 bytes; Khmer characters take 3 bytes each in UTF-8. */
    private static final int MAX_PASSWORD_BYTES = 72;

    private final UserRepository users;
    private final PasswordEncoder passwords;

    public AuthService(UserRepository users, PasswordEncoder passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    @Transactional
    public User register(AuthRequests.Register req) {
        String email = normalize(req.email());
        if (req.password().getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw ApiException.badRequest("password_too_long");
        }
        if (users.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "email_taken");
        }
        try {
            return users.saveAndFlush(new User(email, passwords.encode(req.password()), req.language()));
        } catch (DataIntegrityViolationException e) {
            // Two sign-ups with the same email at the same moment
            throw new ApiException(HttpStatus.CONFLICT, "email_taken");
        }
    }

    @Transactional(readOnly = true)
    public User login(AuthRequests.Login req) {
        if (req.password().getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "bad_credentials");
        }
        return users.findByEmail(normalize(req.email()))
                .filter(u -> passwords.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "bad_credentials"));
    }
}
