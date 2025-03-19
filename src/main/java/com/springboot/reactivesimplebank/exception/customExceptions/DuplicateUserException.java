package com.springboot.reactivesimplebank.exception.customExceptions;

public class DuplicateUserException extends GlobalException {
    public DuplicateUserException(String message) {
        super(message);
    }
}
