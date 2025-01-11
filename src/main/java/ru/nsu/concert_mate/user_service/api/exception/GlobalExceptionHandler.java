package ru.nsu.concert_mate.user_service.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.nsu.concert_mate.user_service.api.users.DetailResponse;
import ru.nsu.concert_mate.user_service.services.cities.CityNotFoundException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.*;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {MailException.class})
    public ResponseEntity<DetailResponse> authException(Exception ex, WebRequest request) {
        log.warn("422 error, incorrect email {}", ex.getMessage());
        DetailResponse detailResponse = new DetailResponse(ex.getMessage());
        return new ResponseEntity<>(detailResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {IncorrectAuthCodeException.class})
    public ResponseEntity<DetailResponse> incorrectAuthCodeException(Exception ex, WebRequest request) {
        log.warn("403 error, incorrect auth code {}", ex.getMessage());
        DetailResponse detailResponse = new DetailResponse(ex.getMessage());
        return new ResponseEntity<>(detailResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<DetailResponse> userNotFoundException(Exception ex, WebRequest request) {
        log.warn("404 error, user not found {}", ex.getMessage());
        DetailResponse detailResponse = new DetailResponse(ex.getMessage());
        return new ResponseEntity<>(detailResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ParseTokenException.class, TokenBlacklistedException.class, TokenExpiredException.class})
    public ResponseEntity<DetailResponse> tokenException(Exception ex, WebRequest request) {
        log.warn("401 error, authentication {}", ex.getMessage());
        DetailResponse detailResponse = new DetailResponse(ex.getMessage());
        return new ResponseEntity<>(detailResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {CityNotFoundException.class})
    public ResponseEntity<DetailResponse> cityNotFoundException(Exception ex, WebRequest request) {
        log.warn("404 error, city not found: {}", ex.getMessage());
        DetailResponse detailResponse = new DetailResponse(ex.getMessage());
        return new ResponseEntity<>(detailResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {CityAlreadyAddedException.class})
    public ResponseEntity<DetailResponse> cityAlreadyAddedException(Exception ex, WebRequest request) {
        log.warn("422 error, city already added: {}", ex.getMessage());
        DetailResponse detailResponse = new DetailResponse(ex.getMessage());
        return new ResponseEntity<>(detailResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {CityNotAddedException.class})
    public ResponseEntity<DetailResponse> cityNotAddedException(Exception ex, WebRequest request) {
        log.warn("422 error, city not added: {}", ex.getMessage());
        DetailResponse detailResponse = new DetailResponse(ex.getMessage());
        return new ResponseEntity<>(detailResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {TrackListAlreadyAddedException.class})
    public ResponseEntity<DetailResponse> trackListAlreadyAddedException(Exception ex, WebRequest request) {
        log.warn("422 error, track list already added: {}", ex.getMessage());
        DetailResponse detailResponse = new DetailResponse(ex.getMessage());
        return new ResponseEntity<>(detailResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {TrackListNotAddedException.class})
    public ResponseEntity<DetailResponse> trackListNotAddedException(Exception ex, WebRequest request) {
        log.warn("422 error, track list not added: {}", ex.getMessage());
        DetailResponse detailResponse = new DetailResponse(ex.getMessage());
        return new ResponseEntity<>(detailResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
