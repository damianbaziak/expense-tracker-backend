package com.example.expensestracker.financialtransaction.api.model;

import com.example.expensestracker.file.service.model.FileData;
import com.example.expensestracker.financialtransaktioncategory.api.model.FinancialTransactionCategory;
import com.example.expensestracker.wallet.api.model.Wallet;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "financial_transactions")
@Entity
public class FinancialTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    // This annotation is used to specify how an enum type should be stored in the database.
    // EnumType.STRING indicates that the enum value will be stored as a string in the database. For example, if the enum
    // value is INCOME, the corresponding value in the database will be the string "INCOME".
    @Enumerated(EnumType.STRING)
    // 'columnDefinition': This is used to define the column type in the database.
    // The columnDefinition specifies that the transaction_type column should be an ENUM type with possible values 'INCOME' or 'EXPENSE'.
    // Note that: columnDefinition is typically used to define the exact SQL fragment for the column, which can be useful for defining
    // database-specific column types like ENUM.
    @Column(name = "transaction_type", columnDefinition = "ENUM('INCOME', 'EXPENSE')")
    private FinancialTransactionType type;

    //@DecimalMin("0,0")
    private BigDecimal amount;

    @Column(name = "transaction_date")
    //@DateTimeFormat(pattern = "yyyy-mm-dd hh-mm-ss")
    private Instant date;

    @ManyToOne(fetch = FetchType.LAZY)
    private FinancialTransactionCategory financialTransactionCategory;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "financialTransaction", orphanRemoval = true)
    private List<FileData> fileData;

    private String description;

    public List<FileData> getFileData() {
        return Collections.unmodifiableList(fileData);
    }


}


