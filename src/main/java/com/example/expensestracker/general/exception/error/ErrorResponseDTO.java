package com.example.expensestracker.general.exception.error;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private String businessCode; // "W001",
    private String businessMessage; //  "WALLETS_RETRIEVING_ERROR",
    private String description; // "Wallet with id: is not found in the database",
    private Integer statusCode; // 404,

    public ErrorResponseDTO(String businessCode, String businessMessage,
                            String description,
                            Integer statusCode) {
        this.businessCode = businessCode;
        this.businessMessage = businessMessage;
        this.description = description;
        this.statusCode = statusCode;
    }
}