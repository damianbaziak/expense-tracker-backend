package com.example.expensestracker.file.service.model;

import com.example.expensestracker.financialtransaction.api.model.FinancialTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Builder
@Table(name = "file_paths")
@AllArgsConstructor
@NoArgsConstructor
public class FileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String type;
    private String filePath;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private FinancialTransaction financialTransaction;

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getType() {
        return type;
    }

    public FinancialTransaction getFinancialTransaction() {
        return financialTransaction;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FileData fileData = (FileData) o;
        return Objects.equals(id, fileData.id) && Objects.equals(fileName, fileData.fileName)
                && Objects.equals(type, fileData.type) && Objects.equals(filePath, fileData.filePath)
                && Objects.equals(financialTransaction, fileData.financialTransaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, type, filePath, financialTransaction);
    }

    @Override
    public String toString() {
        return "FileData{" +
                "id=" + id +
                ", name='" + fileName + '\'' +
                ", type='" + type + '\'' +
                ", filePath='" + filePath + '\'' +
                ", financialTransaction=" + financialTransaction +
                '}';
    }
}

