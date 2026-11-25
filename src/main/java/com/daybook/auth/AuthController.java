package com.daybook.auth;

import com.daybook.user.MeDto;
import com.daybook.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;
    private final TokenService tokens;

    public AuthController(AuthService auth, TokenService tokens) {
        this.auth = auth;
        this.tokens = tokens;
    }

    @PostMapping("/register")
    ResponseEntity<MeDto> register(@Valid @RequestBody AuthRequests.Register req) {
        User user = auth.register(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, tokens.loginCookie(user.getId()).toString())
                .body(MeDto.from(user));
    }

    @PostMapping("/login")
    ResponseEntity<MeDto> login(@Valid @RequestBody AuthRequests.Login req) {
        User user = auth.login(req);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokens.loginCookie(user.getId()).toString())
                .body(MeDto.from(user));
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, tokens.logoutCookie().toString())
                .build();
    }
}
