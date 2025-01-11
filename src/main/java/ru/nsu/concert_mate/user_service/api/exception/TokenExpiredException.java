package ru.nsu.concert_mate.user_service.api.exception;

public class TokenExpiredException extends Exception {
    public TokenExpiredException(String token) {
        super("Token: " + token + " is expired");
    }
}

