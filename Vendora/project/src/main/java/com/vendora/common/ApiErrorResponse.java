package com.vendora.common;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ApiErrorResponse {
    private boolean success;
    private String message;
    private HttpStatus status;
    private LocalDateTime timestamp;
    private List<String> errors;

    public ApiErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.success = false;
    }

    public ApiErrorResponse(String message) {
        this();
        this.message = message;
    }

    public ApiErrorResponse(HttpStatus status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public ApiErrorResponse(HttpStatus status, String message, List<String> errors) {
        this(status, message);
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
