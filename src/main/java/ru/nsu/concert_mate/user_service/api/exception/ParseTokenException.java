package ru.nsu.concert_mate.user_service.api.exception;

public class ParseTokenException extends Exception {
    public ParseTokenException(String token) {
        super("Can't parse token: " + token);
    }
}

