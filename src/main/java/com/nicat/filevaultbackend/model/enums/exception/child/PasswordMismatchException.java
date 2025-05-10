package com.nicat.filevaultbackend.model.enums.exception.child;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
