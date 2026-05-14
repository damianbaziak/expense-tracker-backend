package com.example.expensestracker.general.exception.error;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponseDescriptionListDTO {
    private String status; // "W001",
    private String message; //  "WALLETS_RETRIEVING_ERROR",
    private List<String> descriptionList; // "Wallet with id: is not found in the database",
    private Integer statusCode; // 404,

    public ErrorResponseDescriptionListDTO(String status, String message, List<String> descriptionList, Integer statusCode) {
        this.status = status;
        this.message = message;
        this.descriptionList = descriptionList;
        this.statusCode = statusCode;
    }
}