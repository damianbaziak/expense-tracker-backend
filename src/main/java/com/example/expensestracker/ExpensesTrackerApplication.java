package com.example.expensestracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.math.BigInteger;

@SpringBootApplication
public class ExpensesTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpensesTrackerApplication.class, args);

        BigDecimal amount = new BigDecimal(100);

    }
}




