package com.daybook.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "daybook")
public record DaybookProperties(
        String jwtSecret,
        int tokenDays,
        boolean cookieSecure,
        List<String> corsOrigins) {

    public DaybookProperties {
        if (tokenDays <= 0) tokenDays = 30;
        if (corsOrigins == null) corsOrigins = List.of();
    }
}
