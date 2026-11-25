package com.daybook.common;

import java.time.LocalDate;
import java.time.ZoneOffset;

public final class Dates {
    private Dates() {}

    /**
     * Latest date a user may record. Phones are often ahead of the server's clock
     * (Cambodia is UTC+7), so allow one day of slack instead of rejecting "today".
     */
    public static LocalDate latestAllowed() {
        return LocalDate.now(ZoneOffset.UTC).plusDays(1);
    }

    public static boolean isFuture(LocalDate date) {
        return date.isAfter(latestAllowed());
    }
}
