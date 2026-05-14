package com.example.expensestracker.general.exception;

public enum ErrorCode {
    U001("U001", "USER_ALREADY_EXISTS", 400),
    U002("U002", "INVALID_CREDENTIALS", 401),
    U003("U003", "USER_NOT_FOUND", 404),
    U004("U004", "ACCESS DENIED", 403),

    FT001("FT001", "FINANCIAL_TRANSACTION_NOT_FOUND", 404),
    FT002("FT002", "FINANCIAL_TRANSACTION_TYPE_DOES_NOT_MATCH_WITH_CATEGORY_TYPE", 400),

    FTC001("FTC001", "FINANCIAL_TRANSACTION_CATEGORY_NOT_FOUND", 404),

    W001("W001", "WALLET_NOT_FOUND", 404),
    W002("W002", "USER_IS_NOT_WALLET_OWNER", 403),

    TEA001("TEA001", "VALIDATION_FAILED", 400),
    TEA002("TEA002", "ENDPOINT_DOES_NOT_EXISTS", 400);


    private final String businessCode;
    private final String businessMessage;
    private final Integer httpStatus;

    ErrorCode(String businessCode, String businessMessage, Integer httpStatus) {
        this.businessCode = businessCode;
        this.businessMessage = businessMessage;
        this.httpStatus = httpStatus;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public String getBusinessMessage() {
        return businessMessage;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }
}