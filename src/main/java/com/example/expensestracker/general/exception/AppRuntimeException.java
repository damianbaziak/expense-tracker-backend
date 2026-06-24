package com.example.expensestracker.general.exception;

public class AppRuntimeException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String businessCode;
    private String businessMessage;
    private final Integer httpStatusCode;
    private final String description;

    public AppRuntimeException(ErrorCode errorCode, String description) {
        super(errorCode.getBusinessMessage());
        this.businessCode = errorCode.getBusinessCode();
        this.httpStatusCode = errorCode.getHttpStatus();
        this.errorCode = errorCode;
        this.description = description;

    }

    public AppRuntimeException(ErrorCode errorCode, String description, Throwable cause) {
        super(errorCode.getBusinessMessage(), cause);
        this.businessCode = errorCode.getBusinessCode();
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
        return super.getMessage();
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getDescription() {
        return description;
    }
}
