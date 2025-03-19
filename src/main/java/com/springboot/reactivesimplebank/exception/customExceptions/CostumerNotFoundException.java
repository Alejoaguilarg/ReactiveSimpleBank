package com.springboot.reactivesimplebank.exception.customExceptions;

public class CostumerNotFoundException extends GlobalException{

    public CostumerNotFoundException(final String message) {
        super(message);
    }
}
