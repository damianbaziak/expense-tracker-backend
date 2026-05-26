package com.example.expensestracker.general.exception.error;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponseDescriptionListDTO {
    private String status; // "W001",
    private String businessMessage; //  "WALLETS_RETRIEVING_ERROR",
    private List<String> descriptionList; // "Wallet with id: is not found in the database",
    private Integer statusCode; // 404,

    public ErrorResponseDescriptionListDTO(String status, String businessMessage, List<String> descriptionList, Integer statusCode) {
        this.status = status;
        this.businessMessage = businessMessage;
        this.descriptionList = descriptionList;
        this.statusCode = statusCode;
    }
}