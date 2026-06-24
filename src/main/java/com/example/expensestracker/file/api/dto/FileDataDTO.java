package com.example.expensestracker.file.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDataDTO {
    private String fileName;
    private String type;
    private Long transactionId;
}
