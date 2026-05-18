package com.example.expensestracker.general.exception;

public class AppRuntimeException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorBusinessCode;
    private final String message;
    private final Integer httpStatusCode;
    private final String description;

    public AppRuntimeException(ErrorCode errorCode, String description) {
        this.errorBusinessCode = errorCode.getBusinessCode();
        this.message = errorCode.getBusinessMessage();
        this.httpStatusCode = errorCode.getHttpStatus();
        this.errorCode = errorCode;
        this.description = description;

    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorBusinessCode() {
        return errorBusinessCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getDescription() {
        return description;
    }
}
