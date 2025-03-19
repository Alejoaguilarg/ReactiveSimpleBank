package com.springboot.reactivesimplebank.exception.model;

import java.time.LocalDateTime;

public class ApiErrorDto {
    private final String message;
    private final int status;
    private final LocalDateTime timestamp;
    private final String path;

    public ApiErrorDto(
            final String message,
            final int status,
            final LocalDateTime timestamp,
            final String path) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }
}
