package com.nicat.filevaultbackend.model.enums.exception.child;

public class DownloadLimitExceededException extends RuntimeException {
    public DownloadLimitExceededException(String message) {
        super(message);
    }
}
