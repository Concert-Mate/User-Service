package ru.nsu.concert_mate.user_service.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.nsu.concert_mate.user_service.api.users.DetailResponse;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {MailException.class})
    public ResponseEntity<DetailResponse> authException(Exception ex, WebRequest request) {
        log.warn("422 error, incorrect email {}", ex.getMessage());
        DetailResponse detailResponse = new DetailResponse(ex.getMessage());
        return new ResponseEntity<>(detailResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
