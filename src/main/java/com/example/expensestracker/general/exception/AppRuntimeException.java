package com.example.expensestracker.general.exception;

public class AppRuntimeException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String businessCode;
    private final String businessMessage;
    private final Integer httpStatusCode;
    private final String description;

    public AppRuntimeException(ErrorCode errorCode, String description) {
        this.businessCode = errorCode.getBusinessCode();
        this.businessMessage = errorCode.getBusinessMessage();
        this.httpStatusCode = errorCode.getHttpStatus();
        this.errorCode = errorCode;
        this.description = description;

    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    @Override
    public String getMessage() {
        return businessMessage;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getDescription() {
        return description;
    }
}
