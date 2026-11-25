package com.daybook.user;

import com.daybook.common.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final UserService service;

    public MeController(UserService service) {
        this.service = service;
    }

    @GetMapping
    MeDto me(@AuthenticationPrincipal Jwt jwt) {
        return service.me(CurrentUser.id(jwt));
    }

    @PutMapping("/settings")
    MeDto updateSettings(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody SettingsRequest req) {
        return service.updateSettings(CurrentUser.id(jwt), req);
    }

    @DeleteMapping("/data")
    MeDto eraseData(@AuthenticationPrincipal Jwt jwt) {
        return service.eraseData(CurrentUser.id(jwt));
    }
}
