package com.springboot.reactivesimplebank.exception.customExceptions;

public class DuplicateEntityException extends GlobalException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}
