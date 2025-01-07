package ru.nsu.concert_mate.user_service.api.exception;

public class IncorrectAuthCodeException extends Exception {
    public IncorrectAuthCodeException(String code) {
        super("Code: " + code + " is incorrect");
    }
}

