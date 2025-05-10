package com.nicat.filevaultbackend.model.enums.exception;

import com.nicat.filevaultbackend.model.enums.exception.child.NotFoundException;
import com.nicat.filevaultbackend.model.enums.exception.child.PasswordMismatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalHandlerException {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleAlreadyExistException(NotFoundException notFoundException) {
        log.error("NotFoundException ->  {}", notFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<String> handlePasswordMismatchException(PasswordMismatchException passwordMismatchException){
        log.error("PasswordMismatchException ->  {}", passwordMismatchException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(passwordMismatchException.getMessage());
    }
}
