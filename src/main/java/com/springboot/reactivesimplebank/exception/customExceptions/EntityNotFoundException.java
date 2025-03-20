package com.springboot.reactivesimplebank.exception.customExceptions;

public class EntityNotFoundException extends GlobalException{

    public EntityNotFoundException(final String message) {
        super(message);
    }
}
