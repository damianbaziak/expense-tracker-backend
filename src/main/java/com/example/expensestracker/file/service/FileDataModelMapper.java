package com.example.expensestracker.file.service;

import com.example.expensestracker.file.api.dto.FileDataDTO;
import com.example.expensestracker.file.service.model.FileData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileDataModelMapper {

    @Mapping(source = "financialTransaction.id", target = "transactionId")
    FileDataDTO mapFileDataEntityToFileDataDTO(FileData fileData);
}
