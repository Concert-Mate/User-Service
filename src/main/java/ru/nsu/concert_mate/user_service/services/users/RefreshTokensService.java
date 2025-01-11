package ru.nsu.concert_mate.user_service.services.users;

public interface RefreshTokensService {

    void blacklistToken(String token);

    boolean isTokenBlacklisted(String token);

}
