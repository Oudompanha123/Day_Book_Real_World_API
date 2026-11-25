package com.daybook.user;

public record MeDto(String email, String businessName, String currency, String language) {
    public static MeDto from(User u) {
        return new MeDto(u.getEmail(), u.getBusinessName(), u.getCurrency(), u.getLanguage());
    }
}
