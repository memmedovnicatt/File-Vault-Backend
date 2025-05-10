package com.nicat.filevaultbackend.model.enums.exception.child;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
