package com.daybook.auth;

import com.daybook.config.DaybookProperties;
import com.daybook.config.SecurityConfig;
import java.time.Duration;
import java.time.Instant;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final DaybookProperties props;

    public TokenService(JwtEncoder encoder, DaybookProperties props) {
        this.encoder = encoder;
        this.props = props;
    }

    public String issue(long userId) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("daybook")
                .subject(Long.toString(userId))
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofDays(props.tokenDays())))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public ResponseCookie loginCookie(long userId) {
        return ResponseCookie.from(SecurityConfig.TOKEN_COOKIE, issue(userId))
                .httpOnly(true)
                .secure(props.cookieSecure())
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(props.tokenDays()))
                .build();
    }

    public ResponseCookie logoutCookie() {
        return ResponseCookie.from(SecurityConfig.TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(props.cookieSecure())
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
    }
}
