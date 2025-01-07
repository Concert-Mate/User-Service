package ru.nsu.concert_mate.user_service.api.exception;

public class TokenBlacklistedException extends Exception {
    public TokenBlacklistedException(String token) {
        super("Token: " + token + " is blacklisted");
    }
}

