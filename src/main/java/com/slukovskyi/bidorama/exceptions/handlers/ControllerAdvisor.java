package com.slukovskyi.bidorama.exceptions.handlers;

import com.slukovskyi.bidorama.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(AlreadyExistsException exception) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.name())
                        .details(exception.getMessage())
                        .time(LocalDateTime.now(ZoneOffset.UTC))
                        .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exception) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .code(HttpStatus.NOT_FOUND.name())
                        .details(exception.getMessage())
                        .time(LocalDateTime.now(ZoneOffset.UTC))
                        .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NullReferenceException.class)
    public ResponseEntity<ErrorResponse> handleNullReferenceException(NullReferenceException exception) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.name())
                        .details(exception.getMessage())
                        .time(LocalDateTime.now(ZoneOffset.UTC))
                        .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUndefinedException(Exception exception) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .details("An unexpected error occurred")
                        .time(LocalDateTime.now(ZoneOffset.UTC))
                        .build();
        log.error("#handleUndefinedException: {}", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnableToBidException.class)
    public ResponseEntity<ErrorResponse> handleUnableToBidException(UnableToBidException exception) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.name())
                        .details(exception.getMessage())
                        .time(LocalDateTime.now(ZoneOffset.UTC))
                        .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .code(HttpStatus.UNAUTHORIZED.name())
                        .details(exception.getMessage())
                        .time(LocalDateTime.now(ZoneOffset.UTC))
                        .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException exception) {
        ErrorResponse errorResponse =
                ErrorResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.name())
                        .details(exception.getMessage())
                        .time(LocalDateTime.now(ZoneOffset.UTC))
                        .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
