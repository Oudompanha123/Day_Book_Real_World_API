package com.daybook.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AuthRequests {
    private AuthRequests() {}

    public record Register(
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(min = 8, max = 72) String password,
            @Pattern(regexp = "en|km") String language) {}

    public record Login(
            @NotBlank @Size(max = 254) String email,
            @NotBlank @Size(max = 72) String password) {}
}
